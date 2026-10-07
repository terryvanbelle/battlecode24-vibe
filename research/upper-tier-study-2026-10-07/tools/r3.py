# The randomized tie-break test (critic, upper-tier study 2026-10-07; kept for the g7kite registration, review 2026-10-07): g_iter7's
# kite score breaks ties by coin flip, so stay vs leave within the tie class is randomized. Reads Cm.java's R rows (run-cm.sh):
#   python3 r3.py [<cm out dir>]   (default: the study's VM run; its out3 R rows equal Cm.java's)
import glob, math, sys, os
from collections import defaultdict
D=sys.argv[1] if len(sys.argv)>1 else '/home/terryvanbelle/cmscratch/out3'
UP=set(l.strip() for l in open(os.path.join(os.path.dirname(os.path.abspath(__file__)), '../../../tools/upper-tier.txt')) if l.strip())
R=defaultdict(list)
for fn in glob.glob(D+'/*.csv'):
    for l in open(fn):
        f=l.strip().split(',')
        if f[0]=='R': R[f[1]].append([int(x) for x in f[2:]])
def opp(t): return t.split('__')[1]
def agg(sel, pred, chs):
    per=[]
    for t in sel:
        v=[0]*6
        for r in R[t]:
            k=r[:7]
            if k[0]!=0 or not pred(k): continue
            for c in chs:
                for i in range(6): v[i]+=r[7+c*6+i]
        per.append(v)
    return per
def diff(pa,pb,i):
    na=sum(v[0] for v in pa); nb=sum(v[0] for v in pb)
    a=sum(v[i] for v in pa)/max(1,na); b=sum(v[i] for v in pb)/max(1,nb)
    sa=math.sqrt(sum((v[i]-a*v[0])**2 for v in pa))/max(1,na); sb=math.sqrt(sum((v[i]-b*v[0])**2 for v in pb))/max(1,nb)
    return a,b,a-b,math.sqrt(sa*sa+sb*sb)
print('TIE class (random tie-break): stay (end in reach) minus kite (end out of reach), us only')
for tier in ('U','R','ALL'):
    sel=[t for t in R if tier=='ALL' or (opp(t) in UP)==(tier=='U')]; G=len(sel)
    for rdys,rn in (((1,),'strike'),((3,5),'recharge')):
        stay_ch=[0,3] if rdys==(1,) else [5,6,7]
        for hb in (0,1,2):
            if rdys==(1,):
                pa=agg(sel,lambda k: k[2] in rdys and k[3]==hb and k[5]==1 and k[6]==2,[0,3]); pb=agg(sel,lambda k: k[2] in rdys and k[3]==hb and k[5]==1 and k[6]==2,[2])
                line='%-3s %-8s hp%d n %5d/%5d'%(tier,rn,hb,sum(v[0] for v in pa),sum(v[0] for v in pb))
                for i,nm in ((1,'hit'),(2,'d3'),(3,'str2')):
                    a,b,d,se=diff(pa,pb,i); line+=' | %s %.3f-%.3f=%+.3f(%.3f)'%(nm,a,b,d,se)
                print(line)
