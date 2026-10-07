# The upper-tier study's aggregate reader (2026-10-07; kept for the g7kite registration's reads, review 2026-10-07). Reads the
# CSVs that run-cm.sh writes (Cm.java):  python3 agg3.py <cm out dir> [all|g|k|r|q|p]   (g: G rows, e.g. stand hits on
# recharging victims; 'us' is the side run-cm.sh took from the replay name)
import sys, math, glob, os
from collections import defaultdict
D=sys.argv[1]; mode=sys.argv[2] if len(sys.argv)>2 else 'all'
UP=set(l.strip() for l in open(os.path.join(os.path.dirname(os.path.abspath(__file__)), '../../../tools/upper-tier.txt')) if l.strip())
G={}; K=defaultdict(list); R=defaultdict(list); Q=defaultdict(list); P=defaultdict(list)
for fn in glob.glob(D+'/*.csv'):
    for l in open(fn):
        f=l.strip().split(',')
        t=f[0]
        if t=='G': G.setdefault(f[1],{})[int(f[2])]=[int(x) for x in f[3:]]
        elif t=='K': K[f[1]].append([int(x) for x in f[2:]])
        elif t=='R': R[f[1]].append([int(x) for x in f[2:]])
        elif t=='Q': Q[f[1]].append([int(x) for x in f[2:]])
        elif t=='P': P[f[1]].append([int(x) for x in f[2:]])
def opp(t): return t.split('__')[1]
tags=[t for t in G if len(G[t])==2]
U=[t for t in tags if opp(t) in UP]; RR=[t for t in tags if opp(t) not in UP]
def won(t): return G[t][0][1]
def L(t): return G[t][0][0]
def ratio(pairs):
    N=sum(p[0] for p in pairs); Dn=sum(p[1] for p in pairs)
    if Dn==0: return float('nan'),float('nan')
    r=N/Dn; v=sum((p[0]-r*p[1])**2 for p in pairs)/Dn**2
    return r, math.sqrt(v*len(pairs)/max(1,len(pairs)-1))
A0,H0,W0,E0=2,26,42,50
GM={
 'step-in hits / hits': lambda g:(g[A0+17],g[A0+0]),
 'stepper dies<=3r / step hits': lambda g:(g[A0+18],g[A0+17]),
 'stand hits on recharging victim / hits': lambda g:(g[A0+19],g[A0+0]),
 'step hits on recharging victim / hits': lambda g:(g[A0+20],g[A0+0]),
 'stand hits on recharging per game': lambda g:(g[A0+19],1),
 'hits per game': lambda g:(g[A0+0],1),
 'kill blows per game': lambda g:(g[A0+2],1),
}
def gshow(sel,label):
    print('\n###',label,'n=',len(sel))
    for m,fn in GM.items():
        a=ratio([fn(G[t][0]) for t in sel]); b=ratio([fn(G[t][1]) for t in sel])
        print('%-40s us %9.3f +-%7.3f  them %9.3f +-%7.3f'%(m,a[0],a[1],b[0],b[1]))
CH=['STRstay','STEPstr','STRkite','STRshift','HEAL','ADV','HOLD','RET','x']
def ktable(rows, sel, pred, title, chs, width, extra=None):
    print('\n##',title)
    res={}
    for s,nm in ((0,'us'),(1,'them')):
        per=[]
        for t in sel:
            v=[0]*(9*width)
            for r in rows[t]:
                if r[0]!=s or not pred(*r[1:-9*width]): continue
                vals=r[-9*width:]
                for i in range(9*width): v[i]+=vals[i]
            per.append(v)
        N=sum(sum(v[c*width] for c in range(9)) for v in per)
        line='%-4s N=%8d '%(nm,N)
        for c in chs:
            n=[v[c*width] for v in per]; tot=[sum(v[k*width] for k in range(9)) for v in per]
            sh=sum(n)/max(1,N); se=math.sqrt(sum((a-sh*b)**2 for a,b in zip(n,tot)))/max(1,N)
            line+='| %s %.3f±%.3f '%(CH[c],sh,se)
            if extra:
                for nm2,idx in extra:
                    line+='%s %.3f '%(nm2, sum(v[c*width+idx] for v in per)/max(1,sum(n)))
        print(line)
if mode in ('all','g'):
    gshow(U,'UPPER'); gshow(RR,'REST')
    for o in sorted(set(opp(t) for t in U)): gshow([t for t in U if opp(t)==o],o)
if mode in ('all','k'):
    # K key: phase,cat,rdy,hpb,bb ; outcomes 9: n,died3,hits,anyHit,kb,td,e10,e4,strikeNext2
    KX=[('d3',1),('hit',3),('e4',7),('e10',6),('sN',8)]
    for tn,sel in (('UPPER',U),('REST',RR)):
        print('\n======== K',tn)
        for hb,hn in ((0,'>=700'),(1,'300-699'),(2,'<300')):
            ktable(K,sel,lambda ph,cat,rdy,hpb,bb,hb=hb: cat==0 and rdy==1 and hpb==hb,'R4 both ready HP '+hn,[0,2,3,6],9,KX)
        for a,an in ((3,'ready NEXT turn'),(5,'ready in 2+ turns')):
            for cat,cn in ((0,'R4'),(1,'R10'),(2,'R20')):
                for hb,hn in ((0,'>=700'),(1,'300-699')):
                    ktable(K,sel,lambda ph,c,rdy,hpb,bb,hb=hb,cat=cat,a=a: c==cat and rdy==a and hpb==hb,'%s action %s, move ready, HP %s'%(cn,an,hn),[4,5,6,7],9,KX)
if mode in ('all','r'):
    # R key: phase,rdy,hpb,outAvail,outNoWorse,kiteStay ; per choice 6: n,anyHit,died3,strikeNext,e4,e10
    RX=[('hit',1),('d3',2),('e4',4),('sN',3)]
    for tn,sel in (('UPPER',U),('REST',RR)):
        print('\n======== R',tn)
        for rdy,rn_ in ((1,'both ready (strike turn)'),(3,'ready next turn'),(5,'ready 2+ turns')):
            for oa in (0,1):
                for onw in (0,1):
                    if oa==0 and onw==1: continue
                    ktable(R,sel,lambda ph,r,hpb,a,b,ks,rdy=rdy,oa=oa,onw=onw: r==rdy and hpb<=1 and a==oa and b==onw,'R4 %s HP>=300 outAvail %d outNoWorse %d'%(rn_,oa,onw),[0,2,3,5,6,7],6,RX)
            for ks in (0,1,2):
                ktable(R,sel,lambda ph,r,hpb,a,b,k,rdy=rdy,ks=ks: r==rdy and hpb<=1 and b==1 and k==ks,'R4 %s HP>=300 outNoWorse 1, our-kite-score stay=%d'%(rn_,ks),[0,2,3,5,6,7],6,RX)
if mode in ('all','q'):
    # Q key: phase,hpb,nReach,killAvail,gb ; per choice 6: n,anyHit,died3,tgtDied2,struckKillable,sumGap
    QX=[('hit',1),('d3',2),('td',3),('kill',4),('gap',5)]
    for tn,sel in (('UPPER',U),('REST',RR)):
        print('\n======== Q',tn)
        for hb in (0,1):
            for ka in (0,1):
                ktable(Q,sel,lambda ph,hpb,nr,k,gb,hb=hb,ka=ka: hpb==hb and nr>0 and k==ka,'R10 both ready HP band %d, reachable tile, killAvail %d'%(hb,ka),[1,4,5,6,7],6,QX)
            for gb in (0,1,2):
                ktable(Q,sel,lambda ph,hpb,nr,k,g,hb=hb,gb=gb: hpb==hb and nr==2 and g==gb,'R10 both ready HP band %d, >=2 reach tiles, our-engage-tile gap bucket %d'%(hb,gb),[1,4,5,6,7],6,QX)
if mode in ('all','p'):
    # P key: phase,cat,hpb,closeAvail,closeTh<=1,bb ; per choice 6: n,anyHit,died3,strikeNext,e4,e10
    PX=[('hit',1),('d3',2),('e10',5),('sN',3)]
    for tn,sel in (('UPPER',U),('REST',RR)):
        print('\n======== P',tn)
        for cat in (1,2):
            for hb in (0,1):
                for ca in (1,):
                    for ct in (0,1):
                        ktable(P,sel,lambda ph,c,hpb,a,b,bb,cat=cat,hb=hb,ca=ca,ct=ct: c==cat and hpb==hb and a==ca and b==ct,'ready next turn, cat R%s HP band %d, close tile free, its threats<=1: %d'%('10' if cat==1 else '20',hb,ct),[4,5,6,7],6,PX)
