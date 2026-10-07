# The upper-tier study's T-row reader (2026-10-07; kept for the g7kite registration's reads, review 2026-10-07). It reads the
# CSVs that run-cm.sh writes (Cm.java):  python3 t6.py <tie|reach|proj|tiew|sig> [<cm out dir>]  (default: the study's VM run)
import glob, math, sys, collections, os
D=sys.argv[2] if len(sys.argv)>2 else '/home/terryvanbelle/cmscratch/out6'
UP=set(l.strip() for l in open(os.path.join(os.path.dirname(os.path.abspath(__file__)), '../../../tools/upper-tier.txt')) if l.strip())
T=collections.defaultdict(list)   # tag -> list of (key tuple, values)
LEN={}
for fn in glob.glob(D+'/*.csv'):
    for l in open(fn):
        if l[0]=='T':
            f=l.rstrip().split(','); k=tuple(int(x) for x in f[2:12]); v=[int(x) for x in f[12:]]
            T[f[1]].append((k,v))
        elif l[0]=='G':
            f=l.split(','); LEN[f[1]]=int(f[3])
def opp(t): return t.split('__')[1]
TAGS=sorted(T)
def tier(t): return 'U' if opp(t) in UP else 'R'
# key: side cat struck aS h6 ks lv endIn inRmax anyOut ; values: 0 n 1 hit 2 d3 3 d5 4 d10 5 d20 6 str2 7 s10 8 k10 9 allyD10 10 enemyD10 11 nA20 12 nE20 13 dmg 14 allyD20 15 enemyD20 16 s20 17 k20 18 kbn 19 d2
def per(sel, pred):
    out=[]
    for t in sel:
        v=[0]*20
        for k,x in T[t]:
            if pred(k):
                for i in range(20): v[i]+=x[i]
        out.append(v)
    return out
def rat(pv,i):
    n=sum(v[0] for v in pv); s=sum(v[i] for v in pv); p=s/max(1,n)
    se=math.sqrt(sum((v[i]-p*v[0])**2 for v in pv))/max(1,n)
    return p,se,n
def diff(pa,pb,i):
    a,sa,na=rat(pa,i); b,sb,nb=rat(pb,i)
    # paired by game: cluster-robust SE of difference of ratios using per-game linearisation
    G=len(pa); 
    za=[(va[i]-a*va[0])/max(1,na) for va in pa]; zb=[(vb[i]-b*vb[0])/max(1,nb) for vb in pb]
    se=math.sqrt(sum((x-y)**2 for x,y in zip(za,zb)))
    return a,b,a-b,se
HPG={'>=700':(0,1),'300-699':(2,3),'<300':(4,5),'850+':(0,),'700-849':(1,),'500-699':(2,),'300-499':(3,),'150-299':(4,),'<150':(5,)}
M=[(18,'kbn'),(19,'d2'),(2,'d3'),(4,'d10'),(5,'d20'),(6,'str2'),(7,'s10'),(8,'k10'),(9,'aD10'),(10,'eD10'),(17,'k20'),(14,'aD20'),(15,'eD20')]
mode=sys.argv[1] if len(sys.argv)>1 else 'tie'
if mode=='tie':
  for cls,cpred in (('strike',lambda k:k[1]==0 and k[2]==1 and k[3]==0),('rech',lambda k:k[1]==0 and k[2]==0 and k[3]>=1),('cat1rech',lambda k:k[1]==1 and k[2]==0 and k[3]>=1),('cat1hurtRdy',lambda k:k[1]==1 and k[2]==0 and k[3]==0 and k[4]>=4)):
    print('== tie class (ks=2), %s: end in reach (stay) minus out of reach (leave); per decision; SE clustered by game'%cls)
    for tr in ('U','R'):
        sel=[t for t in TAGS if tier(t)==tr]; G=len(sel)
        for hn in ('>=700','300-699','<300','850+','700-849','500-699','300-499','150-299','<150'):
            hs=HPG[hn]
            pa=per(sel,lambda k:k[0]==0 and cpred(k) and k[4] in hs and k[5]==2 and k[7]==1)
            pb=per(sel,lambda k:k[0]==0 and cpred(k) and k[4] in hs and k[5]==2 and k[7]==0)
            na=sum(v[0] for v in pa); nb=sum(v[0] for v in pb)
            if na+nb<50: continue
            line='%s %-7s n %6d/%6d (in share %.2f, per game %.1f)'%(tr,hn,na,nb,na/max(1,na+nb),(na+nb)/G)
            for i,nm in M:
                a,b,d,se=diff(pa,pb,i); line+=' | %s %+.3f(%.3f)'%(nm,d,se)
            print(line)
elif mode=='reach':
  # decisions the lever changes: side 0, cat 0/1, kite-branch gate (struck or recharging or hurt), lever best all out (lv==0), base best includes in (ks 1/2), ended in reach
  print('== lever reach per game: decisions ending in reach where the W=300 score picks only out-of-reach tiles')
  for tr in ('U','R'):
    sel=[t for t in TAGS if tier(t)==tr]; G=len(sel)
    for hn in ('850+','700-849','500-699','300-499','150-299','<150'):
        hs=HPG[hn]; line='%s %-7s'%(tr,hn)
        for cat in (0,1):
          for ks in (1,2):
            gate=lambda k:(k[2]==1 or k[3]>=1 or k[4]>=4)
            pv=per(sel,lambda k:k[0]==0 and k[1]==cat and gate(k) and k[4] in hs and k[5]==ks and k[6]==0 and k[7]==1)
            n=sum(v[0] for v in pv); line+=' | cat%d ks%d %6.1f/g (kbn %.3f d10 %.3f)'%(cat,ks,n/G,sum(v[18] for v in pv)/max(1,n),sum(v[4] for v in pv)/max(1,n))
        # ks==1 lv==1: forced, no out tile without extra threat
        pv=per(sel,lambda k:k[0]==0 and k[1]==0 and (k[2]==1 or k[3]>=1 or k[4]>=4) and k[4] in hs and k[5]==1 and k[6]==1 and k[7]==1)
        line+=' | cat0 ks1 lv1 %.1f/g'%(sum(v[0] for v in pv)/G)
        pv=per(sel,lambda k:k[0]==0 and k[1]==0 and (k[2]==1 or k[3]>=1 or k[4]>=4) and k[4] in hs and k[5]==1 and k[6]==0 and k[7]==1 and k[8]>=4)
        line+=' | inR>=4 %.2f/g'%(sum(v[0] for v in pv)/G)
        print(line)
  # endIn share by class (does our replay score reproduce the bot's choice?)
  print('== end-in-reach share by base class (side 0, cat 0, gate)')
  for tr in ('U','R'):
    sel=[t for t in TAGS if tier(t)==tr]
    for ks in (0,1,2):
        pin=per(sel,lambda k:k[0]==0 and k[1]==0 and (k[2]==1 or k[3]>=1) and k[5]==ks and k[7]==1); pout=per(sel,lambda k:k[0]==0 and k[1]==0 and (k[2]==1 or k[3]>=1) and k[5]==ks and k[7]==0)
        a=sum(v[0] for v in pin); b=sum(v[0] for v in pout); print('  %s ks%d end-in share %.3f (n %d)'%(tr,ks,a/max(1,a+b),a+b))
if mode=='proj':
  CL={'strike':lambda k:k[1]==0 and k[2]==1 and k[3]==0,
      'rech':lambda k:k[1]==0 and k[2]==0 and (k[3]>=1 or k[4]>=4),
      'cat1':lambda k:k[1]==1 and k[2]==0 and (k[3]>=1 or k[4]>=4)}
  MM=[(18,'kbn'),(19,'d2'),(2,'d3'),(4,'d10'),(5,'d20'),(7,'s10'),(8,'k10'),(9,'aD10'),(10,'eD10'),(16,'s20'),(17,'k20'),(14,'aD20'),(15,'eD20')]
  print('== projection per game: changed decisions x (leave - stay) tie-class effect; SE from the effect only')
  for tr in ('U','R'):
    sel=[t for t in TAGS if tier(t)==tr]; G=len(sel)
    tot={'<700':collections.defaultdict(float),'all':collections.defaultdict(float)}; totv={'<700':collections.defaultdict(float),'all':collections.defaultdict(float)}
    for hn in ('850+','700-849','500-699','300-499','150-299','<150'):
      hs=HPG[hn]; hs3=HPG['>=700'] if hs[0]<=1 else HPG['300-699'] if hs[0]<=3 else HPG['<300']
      line='%s %-7s'%(tr,hn); acc=collections.defaultdict(float); accv=collections.defaultdict(float); nch=0
      for cn,cp in CL.items():
        ch=per(sel,lambda k:k[0]==0 and cp(k) and k[4] in hs and k[5] in (1,2) and k[6]==0 and k[7]==1)
        c=sum(v[0] for v in ch)/G; nch+=c
        ehs=hs if cn!='cat1' else hs3
        pa=per(sel,lambda k:k[0]==0 and cp(k) and k[4] in ehs and k[5]==2 and k[7]==1)
        pb=per(sel,lambda k:k[0]==0 and cp(k) and k[4] in ehs and k[5]==2 and k[7]==0)
        for i,nm in MM:
          a,b,d,se=diff(pa,pb,i); acc[nm]+=c*(-d); accv[nm]+=(c*se)**2
      line+=' changed %.1f/g'%nch
      for i,nm in MM:
        line+=' | %s %+.2f(%.2f)'%(nm,acc[nm],math.sqrt(accv[nm]))
        for key in (('<700',) if hs[0]>=2 else ())+('all',):
          tot[key][nm]+=acc[nm]; totv[key][nm]+=accv[nm]
      print(line)
    for key in ('<700','all'):
      print('%s TOTAL HP %s: '%(tr,key)+' | '.join('%s %+.1f(%.1f)'%(nm,tot[key][nm],math.sqrt(totv[key][nm])) for i,nm in MM))
if mode=='tiew':
  # within-game estimator (each game's in-minus-out difference, weighted n_in*n_out/(n_in+n_out)); jackknife-free SE from the weighted spread
  MM=[(18,'kbn'),(2,'d3'),(4,'d10'),(5,'d20'),(6,'str2'),(7,'s10'),(8,'k10'),(16,'s20'),(17,'k20'),(9,'aD10'),(10,'eD10')]
  CLW={'cat0':lambda k:k[1]==0 and (k[2]==1 or k[3]>=1 or k[4]>=4),'strike':lambda k:k[1]==0 and k[2]==1 and k[3]==0,'rech':lambda k:k[1]==0 and k[2]==0 and (k[3]>=1 or k[4]>=4)}
  for cn in ('cat0','strike','rech'):
    cp=CLW[cn]
    print('== within-game tie estimator, %s, stay minus leave'%cn)
    for tr in ('U','R'):
      sel=[t for t in TAGS if tier(t)==tr]
      for hn in ('850+','700-849','500-699','300-499','150-299','<150','>=700','300-699','<300'):
        hs=HPG[hn]
        pa=per(sel,lambda k:k[0]==0 and cp(k) and k[4] in hs and k[5]==2 and k[7]==1)
        pb=per(sel,lambda k:k[0]==0 and cp(k) and k[4] in hs and k[5]==2 and k[7]==0)
        line='%s %-7s'%(tr,hn)
        for i,nm in MM:
          ws=0; acc=0; terms=[]
          for va,vb in zip(pa,pb):
            if va[0]==0 or vb[0]==0: continue
            w=va[0]*vb[0]/(va[0]+vb[0]); dd=va[i]/va[0]-vb[i]/vb[0]; ws+=w; acc+=w*dd; terms.append((w,dd))
          est=acc/max(1e-9,ws); se=math.sqrt(sum((w*(dd-est))**2 for w,dd in terms))/max(1e-9,ws)
          line+=' | %s %+.3f(%.3f)'%(nm,est,se)
        print(line)
if mode=='sig':
  # proxy of reachEndFree: strike turns (cat 0, struck, action ready at turn start), free exit = the W=300 score picks only out-of-reach tiles (lv 0)
  print('== reachEndFree proxy (our side): end in reach / strike turns with a free exit; per game mean of N and D')
  for tr in ('U','R'):
    sel=[t for t in TAGS if tier(t)==tr]; G=len(sel)
    for nm,hs in (('HP>=300 (synthesis)',(0,1,2,3)),('HP<700 incl hurt',(2,3,4,5)),('HP 300-699',(2,3)),('HP<300',(4,5)),('HP>=700',(0,1))):
      for cls,cp in (('strike',lambda k:k[1]==0 and k[2]==1 and k[3]==0),('strike+rech',lambda k:k[1]==0 and (k[2]==1 or k[3]>=1))):
        pn=per(sel,lambda k:k[0]==0 and cp(k) and k[4] in hs and k[6]==0 and k[7]==1); pd=per(sel,lambda k:k[0]==0 and cp(k) and k[4] in hs and k[6]==0)
        N=sum(v[0] for v in pn); Dn=sum(v[0] for v in pd)
        # per-game shares for the cell SD
        sh=[a[0]/b[0] for a,b in zip(pn,pd) if b[0]>0]
        m=sum(sh)/len(sh); s=math.sqrt(sum((x-m)**2 for x in sh)/(len(sh)-1))
        print('  %s %-20s %-11s pooled %.3f  N %.1f/g D %.1f/g  per-game share mean %.3f sd %.3f'%(tr,nm,cls,N/max(1,Dn),N/G,Dn/G,m,s))
    # enemy side for reference
    pn=per(sel,lambda k:k[0]==1 and k[1]==0 and k[2]==1 and k[3]==0 and k[4]<=3 and k[6]==0 and k[7]==1); pd=per(sel,lambda k:k[0]==1 and k[1]==0 and k[2]==1 and k[3]==0 and k[4]<=3 and k[6]==0)
    print('  %s opponents HP>=300 strike: %.3f'%(tr,sum(v[0] for v in pn)/max(1,sum(v[0] for v in pd))))
if mode=='sig':
  print('== consequence column proxy: hit before next turn (and killed before next turn, died within 3/10 rounds) per free-exit decision, HP<700, our side')
  for tr in ('U','R'):
    sel=[t for t in TAGS if tier(t)==tr]; G=len(sel)
    for cls,cp in (('strike',lambda k:k[1]==0 and k[2]==1 and k[3]==0),('strike+rech',lambda k:k[1]==0 and (k[2]==1 or k[3]>=1 or k[4]>=4))):
      for nm,hs in (('HP<700',(2,3,4,5)),('HP 300-699',(2,3)),('HP<300',(4,5))):
        pall=per(sel,lambda k:k[0]==0 and cp(k) and k[4] in hs and k[6]==0)
        pin=per(sel,lambda k:k[0]==0 and cp(k) and k[4] in hs and k[6]==0 and k[7]==1)
        pout=per(sel,lambda k:k[0]==0 and cp(k) and k[4] in hs and k[6]==0 and k[7]==0)
        tin=per(sel,lambda k:k[0]==0 and cp(k) and k[4] in hs and k[5]==2 and k[7]==1); tout=per(sel,lambda k:k[0]==0 and cp(k) and k[4] in hs and k[5]==2 and k[7]==0)
        line='  %s %-11s %-10s D %.1f/g'%(tr,cls,nm,sum(v[0] for v in pall)/G)
        for i,mn in ((1,'hit'),(18,'kbn'),(2,'d3'),(4,'d10')):
          a,_,_=rat(pall,i); bi,_,_=rat(pin,i); bo,_,_=rat(pout,i); ti,_,_=rat(tin,i); to,_,_=rat(tout,i)
          inshare=sum(v[0] for v in pin)/max(1,sum(v[0] for v in pall))
          arm=a-inshare*(ti-to)    # all in-reach enders switch at the tie-class effect
          line+=' | %s twin %.3f (in %.3f out %.3f; tie in-out %+.3f) arm~%.3f ratio %.2f'%(mn,a,bi,bo,ti-to,arm,arm/max(1e-9,a))
        print(line)
