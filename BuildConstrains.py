import datetime #as dt
print(datetime.datetime.now().strftime("%d-%m-%Y_%H:%M"))

import csv
#import networkx as nx
#G = nx.Graph()
import numpy as np

fileName="graph0.csv"
#fileName="graph1.csv"
fileName="graph11.csv"
#fileName="graph2.csv"
#fileName="graph3.csv"

print(fileName)

EMPTY_EL=-1
EMPTY_TURTLE=(EMPTY_EL,EMPTY_EL)

#-------------------
def int2BinStr(i):
    return bin(i)[2:]
    
#-------------------
def print_list_xy(prefix, list_xy):
    """
    prefix: str
    list_xy[](xbit, ybit)
    """
    print(prefix+':', list_xy)
 
    lp=[]
    for i in range(len(list_xy)):
        lp.append( (int2BinStr(list_xy[i][0]),int2BinStr(list_xy[i][1])) )
    print(' '*len(prefix), lp)

#-------------------
def graph_input(fileName):
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
#def make_graph_dual(graph)

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
def get_list_index(l, e):
# NOT USED !
#    print('lcl',l)
#    print('e',e)
    for k in range(len(l)):
        if l[k]==e: return k
    
    return -1    

#-------------------
def get_indx_in_col(e,matr,col):
    for k in range(len(matr)):
        if matr[k][col]==e: return k
    
    return -1  
    
#-------------------
def xbit(indx):
    return 2**indx # 1<<indx

#-------------------
def sort_y_matrG(matrG, lX, lY):
    """
    matrG[x][y]
    lX[x]
    lY[y]
    -
    binMG[](binY,kx),inlX[],inMG[][]: binMG, lX, matrG
    """
#Tst    print(' sort_y_matrG')
    binMG=[]
    nX = len(lX)
    nY = len(lY)
#Tst   print('matrG:',matrG)
#Tst    print('lX:',nX,lX)
#Tst    print('lY:',nY,lY)
    inlX = [-1 for i in range(nX) ]
    inMG = [ [0]*nY for i in range(nX) ]
    
    for kx in range(len(matrG)): #.keys():
        binY=0
        for ky in range(len(matrG[kx])):
            if matrG[kx][ky] == 1:
                binY += xbit(ky)
        binMG.append((binY,kx))  
 
#Tst   print('binMG-bs:',binMG)
    binMG.sort()
#Tst    print('binMG-as:',binMG)
    
    for kx in range(len(binMG)):
       inlX[kx] = lX[binMG[kx][1]] 
       inMG[kx] = matrG[binMG[kx][1]]

#Tst    print('inlX:',inlX)
#Tst    print('inMG:',inMG)
    return binMG, inlX, inMG

#-------------------
def get_x_connection(indx, binMG):
    """
    binMG[](bitY,kx)
    return (xbit, ybit)
    """
    x=xbit(indx)
    y=binMG[indx][0]
#Tst     print(' x,y current:',int2BinStr(x),int2BinStr(y))
    return ((x, y))
    
#-------------------
def get_sorted_prev_connections(indx,npQX,binMG):
    """
    It get previous connected vertexis  for current vertex 
    indx: int
    npQX[kx][kx]
    binMG[](bitY,kx)
    -
    prev_connections[](xbit, ybit)
    """
#Tst     print(' get_sorted_prev_connections')
#Tst     print(f'npQX[{indx}]',npQX[indx])
    prev_connections=[]
    
    for i in range(indx):
        if npQX[indx][i] == 1:
            prev_connections.append((xbit(i), binMG[i][0]))  # get_x_connection
    
#Tst     print('indx,prev_connections',indx,prev_connections)
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
#Tst     print(' get_prev_connected_limits')
#Tst     print_list_xy('xPrevConnections-s',xPrevConnections)
#Tst     print_list_xy('listLimits',listLimits)
    
    prev_limits=[]
    for i in range(len(xPrevConnections)) :
        xPrev = xPrevConnections[i][0]
#Tst         print('i,xPrev:',i,xPrev)
        for k in range(len(listLimits)) :
            if listLimits[k][0] & xPrev > 0 :
                prev_limits.append(listLimits[k])
          
#Tst     print_list_xy('prev_limits',prev_limits)
    return prev_limits
    
#-------------------
def add_xy_bits_to_prev(indx,binMG,xPrevLimitList):
    """
    adding current x & y to each previous limit
    binMG[](bitY,kx)
    xPrevLimitList[](xbit, ybit)
    """
#Tst     print(' add_xy_bits_to_prev')
#Tst     print_list_xy('xPrevLimitList-s',xPrevLimitList)
    x=xbit(indx)
    y=binMG[indx][0]
#Tst     print('x,y:',int2BinStr(x),int2BinStr(y))
    
    for i in range(len(xPrevLimitList)):
        xPrevLimitList[i] = (xPrevLimitList[i][0] | x, xPrevLimitList[i][1] | y) 
    
#Tst     print_list_xy('xPrevLimitList-f',xPrevLimitList)
    return xPrevLimitList

#-------------------
def check_dupl_y_in_prev(xPrevLimitList):
    """
    check and clear previous limits for duplicated y
    xPrevLimitList[](xbit, ybit)
    """
#Tst     print(' check_dupl_y_in_prev')
#Tst     print_list_xy('xPrevLimitList-s',xPrevLimitList)
    lenP = len(xPrevLimitList)
    
    for kp in range(lenP-1, -1, -1):
        yp = xPrevLimitList[kp][1]
        if yp == EMPTY_TURTLE: continue
        for kl in range(kp-1, -1, -1):
            if xPrevLimitList[kl][1] == yp:
                xPrevLimitList[kl] = EMPTY_TURTLE    
    
#Tst     print_list_xy('xPrevLimitList-e',xPrevLimitList)
    
#   clear limits 
    for kp in range (lenP-1, -1, -1):
        if xPrevLimitList[kp] == EMPTY_TURTLE:
            del xPrevLimitList[kp]

#Tst     print_list_xy('xPrevLimitList-f',xPrevLimitList)
    return xPrevLimitList

#-------------------
def check_prev_y_in_limits(listLimits,xPrevLimitList):
    """
    check and clear common limits for duplicated y by previous limits
    listLimits[](xbit, ybit)
    xPrevLimitList[](xbit, ybit)
    """
#Tst     print(' check_prev_y_in_limits')
#Tst     print_list_xy('listLimits-s',listLimits)
#Tst     print_list_xy('xPrevLimitList',xPrevLimitList)
    lenP = len(xPrevLimitList)
    lenL = len(listLimits)
    
    for kp in range(lenP):
        yp = xPrevLimitList[kp][1]
        for kl in range(lenL):
            if listLimits[kl][1] == yp:
                listLimits[kl] = EMPTY_TURTLE
                
#Tst     print_list_xy('listLimits-e',listLimits)            

#   clear limits                
    for kl in range (lenL-1, -1, -1):
        if listLimits[kl] == EMPTY_TURTLE:
            del listLimits[kl]
        
#Tst     print_list_xy('listLimits-f',listLimits)    
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
        if indx == 0 :
            connection=get_x_connection(indx, binMG)
            listLimits.append(connection)
#           print_list_xy('after append listLimits for 0',listLimits)
            continue
        else:
            xPrevConnections = get_sorted_prev_connections(indx,npQX,binMG)
            if len(xPrevConnections) > 0 :
                xPrevLimitList = get_prev_connected_limits(listLimits, xPrevConnections)
            else: # First vertex in connected component
                xPrevLimitList = []
            
            connection=get_x_connection(indx, binMG)
            listLimits.append(connection)
            
            if len(xPrevLimitList) > 0 :
                xPrevLimitList = add_xy_bits_to_prev(indx,binMG,xPrevLimitList)
                xPrevLimitList = check_dupl_y_in_prev(xPrevLimitList)
                listLimits = check_prev_y_in_limits(listLimits,xPrevLimitList)
                listLimits.extend(xPrevLimitList)
#Tst                 print_list_xy('after extend listLimits',listLimits)
                    
    return listLimits

#-------------------
def str_to_limit_side(prefix,bitStr,listEdjes):
    """
    prefix str
    bitStr
    listEdjes[][edje, edjeIndxInLimit]
    """
    global dual
    
    s = ''
    cnt = 0
    listBit = list(bitStr)
    listBit.reverse()
    print('listBit:',listBit)
    print('listEdjes:',listEdjes)
    n = len(listBit)
    for i in range(n):
        if dual:
            k = (n-1)-i
        else:
            k = i
            
        if listBit[k] == '1' :
            if cnt > 0 :
                s += ' + '
            j=get_indx_in_col(k,listEdjes,1)
            s += prefix + listEdjes[j][0]
            cnt = cnt + 1
    
    print('s:',s)
    return s

#-------------------
def sort_list_by_val(lst):

    print(' sort_list_by_val')
    #lst_out: [][lst_el, old_indx]
    lst_out=[]
    for i in range(len(lst)):
        lst_out.append([lst[i],i])
    
    print('lst_out-s:',lst_out)
    lst_out.sort()
    
    print('lst_out-f:',lst_out)
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
 #Tst       print('l:',l)
        sl,sr = '',''
        xa = int2BinStr(l[0])
        yb = int2BinStr(l[1])
 #Tst       print('xa,yb:',xa,yb)
 
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
    print('dual:',dual)
    lX=[]
    nX=0
    lY=[]
    nY=0

    lXold, lYold = gen_edjes(graph)
    if dual:
        lY, lX = lXold, lYold
    else:
        lX, lY = lXold, lYold
            
    nX=len(lX)
    nY=len(lY)
    print('lX:',nX,lX)
    print('lY:',nY,lY)

    matrG = [ [0]*nY for i in range(nX) ]

    for keyX in graph.keys():
        indX=lXold.index(keyX)
        for y in graph[keyX]:
            indY=lYold.index(y)
            
            if dual:
                matrG[indY][indX] = 1
            else:
                matrG[indX][indY] = 1

#Tst    print('matrG:',matrG)

    binMG, lX, matrG = sort_y_matrG(matrG, lX, lY)
    print(' after sort_y_matrG')
    print('binMG:',binMG)
    print('lX:',nX,lX)
    print('matrG:',matrG)

    npMG = np.array(matrG)
#Tst    print('npMG:',npMG)
    #print('npMGT',npMG.transpose())
    # if dual:
        # npMG = npMG.transpose()
#    print('npMG:',npMG)
    #print('npMGT',npMG.transpose()) 
    npMX = np.dot(npMG, npMG.transpose())
    #Tst print(' after np.dot')
    #Tst print('npMX:',npMX)
    npQX = np.minimum(npMX, 1)
#Tst    print('npQX:',npQX)

    listR = []

    listR = create_limits(npQX, binMG)
#Tst    print_list_xy('listR',listR)

    print_limits(listR, lX, lY, dual)

#-------------------
def graph_create_dual(graph):
    graph_dual = dict()

    geX=[]
    geY=[]
    
 #   print('graph_ge',graph)
    for keyX in graph.keys(): # new y
#        print('keyX',keyX)
        # if not check_list(geX, keyX):
            # geX.append(keyX)
        
        for y in graph[keyX]: # new x
#            print('y',y)
            # if not check_list(geY, y):
                # geY.append(y)
            
    
#    print('geX:',geX)
#    print('geY:',geY)    
#-------------------
#-------------------

graph = dict()
graph_dual = dict()

graph = graph_input(fileName)
#Tst print(' after graph_input')
print('graph:',graph)

dual = False
build_net_limits(dual,graph)
#graph_dual = make_graph_dual(graph)
#print('graph_dual:',graph_dual)
dual = True
build_net_limits(dual,graph)    


