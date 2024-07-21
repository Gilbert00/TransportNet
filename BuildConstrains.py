import sys, getopt
import datetime #as dt
#print(datetime.datetime.now().strftime("%d-%m-%Y_%H:%M"))

import csv
#import networkx as nx
#G = nx.Graph()
import numpy as np

#fileName="graph0.csv"
#fileName="graph1.csv"
#fileName="graph11.csv"
#fileName="graph2.csv"
#fileName="graph3.csv"

#print(fileName)

EMPTY_EL=-1
EMPTY_TURTLE=(EMPTY_EL,EMPTY_EL)

TST=0

#-------------------
def int2BinStr(i):
    return bin(i)[2:]
    
#-------------------
def print_list_xy(prefix, list_xy):
    """
    prefix: str
    list_xy[](xbin, ybin)
    """
    print(prefix+':', list_xy)
 
    lp=[]
    for i in range(len(list_xy)):
        lp.append( (int2BinStr(list_xy[i][0]),int2BinStr(list_xy[i][1])) )
    print(' '*len(prefix), lp)

#-------------------
def graph_input(fileName):
    graph = dict()
    with open(fileName, mode ='r') as csv_file:
        fileReader = csv.reader(csv_file)
        for row in fileReader:
 #           print('row',row)
            if len(row)==0: continue
            graph[row[0]] = [row[i] for i in range(1, len(row))]

 #   print()
 #   print(graph)
    return graph 
    
#-------------------
def check_list(l, e):
#    print('lcl',l)
#    print('e',e)
    for k in l:
        if len(l)>0 and k==e: return True
    
    return False    

#-------------------
def gen_edjes(graph):
    geX=[]
    geY=[]
    
 #   print('graph_ge',graph)
    for keyX in graph.keys():
#        print('keyX',keyX)
        if not check_list(geX, keyX):
            geX.append(keyX)
        
        for y in graph[keyX]:
#            print('y',y)
            if not check_list(geY, y):
                geY.append(y)
    
#    print('geX:',geX)
#    print('geY:',geY)
    return geX, geY

#-------------------
# def get_list_index(l, e):
# # NOT USED !
# #    print('lcl',l)
# #    print('e',e)
    # for k in range(len(l)):
        # if l[k]==e: return k
    
    # return -1    

#-------------------
def get_indx_in_col(e,matr,col):
    for k in range(len(matr)):
        if matr[k][col]==e: return k
    
    return -1  
    
#-------------------
def xbit(indx):
    return 2**indx # 1<<indx

#-------------------
def sort_matrG_by_x(matrG, lX, lY):
    """
    matrG[x][y]
    lX[x]
    lY[y]
    -
    inbinMG[](binY,kx),inlX[],inmatrMG[][]: binMG, lX, matrG
    """
    if TST in [1]: print(' sort_matrG_by_x')
    inbinMG=[]
    nX = len(lX)
    nY = len(lY)
    if TST in [1]: print('matrG:',matrG)
    if TST in [1]: print('lX:',nX,lX)
    if TST in [1]: print('lY:',nY,lY)
    inlX = [-1 for i in range(nX) ]
    inmatrMG = [ [0]*nY for i in range(nX) ]
    
    for kx in range(len(matrG)): #.keys():
        binY=0
        for ky in range(len(matrG[kx])):
            if matrG[kx][ky] == 1:
                binY += xbit(ky)
        inbinMG.append((binY,kx))  
 
    if TST in [1]: print('inbinMG-bs:',binMG)
# for connected graph!!!   inbinMG.sort()
    if TST in [1]: print('inbinMG-as:',binMG)
    
    for kx in range(len(inbinMG)):
       inlX[kx] = lX[inbinMG[kx][1]] 
       inmatrMG[kx] = matrG[inbinMG[kx][1]]

    if TST in [1]: print('inlX:',inlX)
    if TST in [1]: print('inMG:',inMG)
    return inbinMG, inlX, inmatrMG

#-------------------
def get_x_connection(indx, binMG):
    """
    binMG[](bitY,kx)
    return (xbit, ybit)
    """
    x=xbit(indx)
    y=binMG[indx][0]
    if TST in [4]: print(' indx,x,y current:',indx,int2BinStr(x),int2BinStr(y))#Tst4
    return ((x, y))
    
#-------------------
def get_sorted_prev_connections(indx,npQX,binMG):
    """
    It get previous connected vertexis  for current vertex 
    indx: int
    npQX[kx][kx]
    binMG[](binY,kx)
    -
    prev_connections[](xbit, ybit)
    """
    if TST in [4]: print(' get_sorted_prev_connections')#Tst
#Tst     print(f'npQX[{indx}]',npQX[indx])
    prev_connections=[]
    
    for i in range(indx):
        if npQX[indx][i] == 1:
            prev_connections.append((xbit(i), binMG[i][0]))  # get_x_connection
    
    if TST in [4]: print('indx,prev_connections',indx,prev_connections)#Tst
    return prev_connections

#-------------------
def get_prev_connected_limits(listLimits, xPrevConnections):
    """
    for each prev connected vertex we get limits with it
    listLimits[](xbit, ybit)
    xPrevConnections[](xbit, ybit)
    -
    prev_limits[](xbit, ybit)
    """
    if TST in [4]: print(' get_prev_connected_limits')#Tst
#Tst     print_list_xy('xPrevConnections',xPrevConnections)
#Tst     print_list_xy('listLimits',listLimits)
    
    prev_limits=[]
    for i in range(len(xPrevConnections)) :
        xPrev = xPrevConnections[i][0]
#Tst         print('i,xPrev:',i,xPrev)
        for k in range(len(listLimits)) :
            if listLimits[k][0] & xPrev > 0 :
                prev_limits.append(listLimits[k])
          
    if TST in [4]: print_list_xy('prev_limits',prev_limits)#Tst
    return prev_limits
    
#-------------------
def add_xy_bits_to_prev(indx,binMG,xPrevLimitList):
    """
    adding current x & y to each previous limit
    binMG[](bitY,kx)
    xPrevLimitList[](xbit, ybit)
    """
    if TST in [4]: print(' add_xy_bits_to_prev')#Tst
    if TST in [4]: print_list_xy('xPrevLimitList-s',xPrevLimitList)#Tst
    x=xbit(indx)
    y=binMG[indx][0]
    if TST in [4]: print('indx,x,y:',indx,int2BinStr(x),int2BinStr(y))#Tst
    
    for i in range(len(xPrevLimitList)):
        xPrevLimitList[i] = (xPrevLimitList[i][0] | x, xPrevLimitList[i][1] | y) 
    
    if TST in [4]: print_list_xy('xPrevLimitList-f',xPrevLimitList)#Tst
    return xPrevLimitList

#-------------------
def check_dupl_y_in_prev(xPrevLimitList):
    """
    check and clear previous limits for duplicated y
    xPrevLimitList[](xbit, ybit)
    """
    if TST in [4]: print(' check_dupl_y_in_prev')#Tst
    if TST in [4]: print_list_xy('xPrevLimitList-s',xPrevLimitList)#Tst
    lenP = len(xPrevLimitList)
    
    for kp in range(lenP-1, -1, -1):
        yp = xPrevLimitList[kp][1]
        if yp == EMPTY_TURTLE: continue
        for kl in range(kp-1, -1, -1):
            if xPrevLimitList[kl][1] == yp:
                xPrevLimitList[kp] = (xPrevLimitList[kp][0] | xPrevLimitList[kl][0], yp) 
                xPrevLimitList[kl] = EMPTY_TURTLE    

#!!!check_dupl_xy(xPrevLimitList)
#   clear list
    
    if TST in [4]: print_list_xy('xPrevLimitList-e',xPrevLimitList)#Tst
    
#   clear previous limits
#   clear list 
    for kp in range (lenP-1, -1, -1):
        if xPrevLimitList[kp] == EMPTY_TURTLE:
            del xPrevLimitList[kp]
            
    if TST in [4]: print_list_xy('xPrevLimitList-f',xPrevLimitList)#Tst
    return xPrevLimitList

#-------------------
def check_prev_y_in_limits(listLimits,xPrevLimitList):
    """
    check and clear common limits for duplicated y by previous limits
    listLimits[](xbit, ybit)
    xPrevLimitList[](xbit, ybit)
    """
    if TST in [4]: print(' check_prev_y_in_limits')#Tst
    if TST in [4]: print_list_xy('xPrevLimitList',xPrevLimitList)#Tst
    if TST in [4]: print_list_xy('listLimits-s',listLimits)#Tst
    lenP = len(xPrevLimitList)
    lenL = len(listLimits)
    
    for kp in range(lenP):
        yp = xPrevLimitList[kp][1]
        for kl in range(lenL):
            if listLimits[kl][1] == yp:
                xPrevLimitList[kp] = (xPrevLimitList[kp][0] | listLimits[kl][0], yp) 
                listLimits[kl] = EMPTY_TURTLE

#!!!check_dupl_xy(xPrevLimitList)
                
    if TST in [4]: print_list_xy('listLimits-e',listLimits)#Tst            

#   clear limits 
#   clear list               
    for kl in range (lenL-1, -1, -1):
        if listLimits[kl] == EMPTY_TURTLE:
            del listLimits[kl]
        
    if TST in [4]: print_list_xy('listLimits-f',listLimits)  #Tst  
    return listLimits

#-------------------
def create_limits(npQX, binMG):
    """
    npQX[kx][kx]
    binMG[](bitY,kx)
    -
    listLimits[](xbit, ybit)
    xPrevConnections[](xbit, ybit)
    connection (xbit, ybit)
    xPrevLimitList[](xbit, ybit)
    """
#Tst     print(' create_limits')
    listLimits = []
    for indx in range(len(npQX)):
        if TST in [4]: print('indQX:',indx)
        if indx == 0 :
            connection=get_x_connection(indx, binMG)
            listLimits.append(connection)
            if TST in [4]: print_list_xy('after append listLimits',listLimits)
            continue
        else:
            xPrevConnections = get_sorted_prev_connections(indx,npQX,binMG)
            if len(xPrevConnections) > 0 :
                xPrevLimitList = get_prev_connected_limits(listLimits, xPrevConnections)
            else: # First vertex in connected component
                xPrevLimitList = []
            
            connection=get_x_connection(indx, binMG)
            listLimits.append(connection)
            if TST in [4]: print_list_xy('after append listLimits',listLimits)
            
            if len(xPrevLimitList) > 0 :
                xPrevLimitList = add_xy_bits_to_prev(indx,binMG,xPrevLimitList)
                xPrevLimitList = check_dupl_y_in_prev(xPrevLimitList)
                listLimits = check_prev_y_in_limits(listLimits,xPrevLimitList)
                listLimits.extend(xPrevLimitList)
                if TST in [4]: print_list_xy('after extend listLimits',listLimits)
                    
    return listLimits

#-------------------
def str_to_limit_side(prefix,bitStr,listEdjes):
    """
    prefix str
    bitStr
    listEdjes[][edje, edjeIndxInLimit]
    """
   
    listBit = list(bitStr)
    listBit.reverse()
#Tst3    print('listBit:',listBit)
#Tst3    print('listEdjes:',listEdjes)
    
    n = len(listBit)
    listS = []
    for i in range(n):
        if listBit[i] == '1' :
            j=get_indx_in_col(i,listEdjes,1)
            listS.append(listEdjes[j][0])
            
    listS.sort()
 #Tst3   print('listS:',listS) 
    
    s = ''
    cnt = 0
    for k in range(len(listS)):
        if cnt > 0 :
            s += ' + '   
        s += prefix + listS[k]
        cnt = cnt + 1
            
 #Tst3   print('s:',s)
    return s

#-------------------
def sort_list_by_val(lst):

#Tst3    print(' sort_list_by_val')
    #lst_out: [][lst_el, old_indx]
    lst_out=[]
    for i in range(len(lst)):
        lst_out.append([lst[i],i])
    
#Tst3    print('lst_out-s:',lst_out)
    lst_out.sort()
    
#Tst3    print('lst_out-f:',lst_out)
    return lst_out

#-------------------
def print_limits(listR, lX, lY, dual):
    """
    print limit in symbolic view
    listR[](xbit, ybit)
    lX[x]
    lY[y]
    dual: logical
    """
        
    lXsort = sort_list_by_val(lX)
    lYsort = sort_list_by_val(lY)
    
    for l in listR:
#Tst3        print('R:',l)
        sl,sr = '',''
        xa = int2BinStr(l[0])
        yb = int2BinStr(l[1])
#Tst3        print('xa,yb:',xa,yb)
 
        if dual:
            sl = str_to_limit_side('b',xa,lXsort)
            sr = str_to_limit_side('a',yb,lYsort)
        else:
            sl = str_to_limit_side('a',xa,lXsort)
            sr = str_to_limit_side('b',yb,lYsort)
        
        print(sl,'<=',sr)

#-------------------
def build_net_limits(dual,graph):
    """
    dual: logical
    graph: 
    """
#Tst    print('dual:',dual)
    lX=[]
    nX=0
    lY=[]
    nY=0

    lXold, lYold = gen_edjes(graph)

    lX, lY = lXold, lYold
            
    nX=len(lX)
    nY=len(lY)
    if TST in [1]: print('lX:',nX,lX)
    if TST in [1]: print('lY:',nY,lY)

    matrG = [ [0]*nY for i in range(nX) ]
#   matrG: [indX][indY]  -> 1 if x connect y
    for keyX in graph.keys():
        indX=lXold.index(keyX)
        for y in graph[keyX]:
            indY=lYold.index(y)
            
            matrG[indX][indY] = 1

    if TST in [1]: print('matrG:',matrG)

    binMG, lX, matrG = sort_matrG_by_x(matrG, lX, lY)
    if TST in [1,4]: print(' after sort_matrG_by_x')#Tst1
    if TST in [1,4]: print('binMG:',binMG)#Tst1
    if TST in [1,4]: print('lX:',nX,lX)#Tst1
    if TST in [1,4]: print('matrG:',matrG)#Tst1

    npMG = np.array(matrG)
#Tst    print('npMG:',npMG)
    npMX = np.dot(npMG, npMG.transpose())
    #Tst print(' after np.dot')
    #Tst print('npMX:',npMX)
    npQX = np.minimum(npMX, 1)
    if TST in [1,4]: print('npQX:',npQX)#Tst

    listR = []

    listR = create_limits(npQX, binMG)
    print_list_xy('listR',listR)

    print_limits(listR, lX, lY, dual)

#-------------------
def graph_create_dual(graph):
    graph_dual = dict()
 
    NON=-1
    
    for keyX in graph.keys(): # new y
 #Tst        print('keyX',keyX)
        
        for y in graph[keyX]: # new x
 #Tst            print('y',y)
            if graph_dual.get(y,NON) == NON :
 #Tst                print('NON')
                graph_dual[y] = []
                graph_dual[y].append(keyX)
            else:
                if not check_list(graph_dual[y], keyX):
 #Tst                    print('append')
                    graph_dual[y].append(keyX)
                else:
                    pass #Error! Dupl edje
        
 #Tst2   print('graph_dual:',graph_dual)
    for key in graph_dual.keys():
        graph_dual[key].sort()

#    print('graph_dual:',graph_dual)
    return graph_dual

#-------------------
def main(argv):

    fileName = argv[1]
    print(fileName)
    
    if len(sys.argv) > 2 :
        nMode = int(argv[2])
    else:
        nMode = 0

    print('mode:',nMode)
    graph = dict()

    graph = graph_input(fileName)
    #Tst print(' after graph_input')
    print('graph:',graph)

    if nMode in (0,1):
        dual = False
        build_net_limits(dual,graph)

    if nMode in (0,2):
        dual = True
        graph_dual = graph_create_dual(graph)
        print('graph_dual:',graph_dual)
        build_net_limits(dual,graph_dual)

    if not nMode in (0,1,2):
        print('Invalid mode !',nMode)

#-------------------
#-------------------
if __name__ == "__main__":
    print(datetime.datetime.now().strftime("%d-%m-%Y_%H:%M"))

    if len(sys.argv) == 1:
        print('Usage: ' + sys.argv[0] + ' Input_File' + ' mode(?)')
    else:
        print(sys.argv)
        main(sys.argv)
