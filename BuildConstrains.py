import datetime #as dt
print(datetime.datetime.now().strftime("%H:%M"))

import csv
#import networkx as nx
#G = nx.Graph()
import numpy as np

fileName="graph0.csv"
#fileName="graph1.csv"
fileName="graph11.csv"
#fileName="graph2.csv"
fileName="graph3.csv"

print(fileName)

graph = dict()
lX=[]
nX=0
lY=[]
nY=0

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
def sort_y_matrG(matrG, lX):
    """
    matrG[x][y]
    lX[x]
    -
    binMG[](binY,kx)
    inMG[]
    """
    binMG=[]
    inlX = [-1 for i in range(nX) ]
    inMG = [ [0]*nY for i in range(nX) ]
    
    for kx in range(len(matrG)): #.keys():
        binY=0
        for ky in range(len(matrG[kx])):
            if matrG[kx][ky] == 1:
                binY += 2**ky
        binMG.append((binY,kx))  
 #   print('binMG-bs:',binMG)
    binMG.sort()
 #   print('binMG-as:',binMG)
    
    for kx in range(len(binMG)):
       inlX[kx] = lX[binMG[kx][1]] 
       inMG[kx] = matrG[binMG[kx][1]] 
    return binMG, inlX, inMG

#-------------------
def xbit(indx):
    return 2**indx # 1<<indx

#-------------------
def get_x_connection(indx, binMG):
    """
    binMG[](bitY,kx)
    return (xbit, ybit)
    """
    x=xbit(indx)
    y=binMG[indx][0]
    print(' x,y current:',int2BinStr(x),int2BinStr(y))
    return ((x, y))
    
#-------------------
def get_sorted_prev_connections(indx,npQX,binMG):
    """
    It get previous connected vertexis  for current vertex 
    npQX[kx][kx]
    binMG[](bitY,kx)
    -
    prev_connections[](xbit, ybit)
    """
    print(' get_sorted_prev_connections')
    print(f'npQX[{indx}]',npQX[indx])
    prev_connections=[]
    
    for i in range(indx):
        if npQX[indx][i] == 1:
            prev_connections.append((xbit(i), binMG[i][0]))  # get_x_connection
    
    print('indx,prev_connections',indx,prev_connections)
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
    print(' get_prev_connected_limits')
    print_list_xy('xPrevConnections-s',xPrevConnections)
    print_list_xy('listLimits',listLimits)
    
    prev_limits=[]
    for i in range(len(xPrevConnections)) :
        xPrev = xPrevConnections[i][0]
        print('i,xPrev:',i,xPrev)
        for k in range(len(listLimits)) :
            if listLimits[k][0] & xPrev > 0 :
                prev_limits.append(listLimits[k])
          
    print_list_xy('prev_limits',prev_limits)
    return prev_limits
    
#-------------------
def add_xy_bits_to_prev(indx,binMG,xPrevLimitList):
    """
    adding current x & y to each previous limit
    binMG[](bitY,kx)
    xPrevLimitList[](xbit, ybit)
    """
    print(' add_xy_bits_to_prev')
    print_list_xy('xPrevLimitList-s',xPrevLimitList)
    x=xbit(indx)
    y=binMG[indx][0]
    print('x,y:',int2BinStr(x),int2BinStr(y))
    
    for i in range(len(xPrevLimitList)):
        xPrevLimitList[i] = (xPrevLimitList[i][0] | x, xPrevLimitList[i][1] | y) 
    
    print_list_xy('xPrevLimitList-f',xPrevLimitList)
    return xPrevLimitList

#-------------------
def check_dupl_y_in_prev(xPrevLimitList):
    """
    check and clear previous limits for duplicated y
    xPrevLimitList[](xbit, ybit)
    """
    print(' check_dupl_y_in_prev')
    print_list_xy('xPrevLimitList-s',xPrevLimitList)
    lenP = len(xPrevLimitList)
    
    for kp in range(lenP-1, -1, -1):
        yp = xPrevLimitList[kp][1]
        if yp == EMPTY_TURTLE: continue
        for kl in range(kp-1, -1, -1):
            if xPrevLimitList[kl][1] == yp:
                xPrevLimitList[kl] = EMPTY_TURTLE    
    
    print_list_xy('xPrevLimitList-e',xPrevLimitList)
    
#   clear limits 
    for kp in range (lenP-1, -1, -1):
        if xPrevLimitList[kp] == EMPTY_TURTLE:
            del xPrevLimitList[kp]

    print_list_xy('xPrevLimitList-f',xPrevLimitList)
    return xPrevLimitList

#-------------------
def check_prev_y_in_limits(listLimits,xPrevLimitList):
    """
    check and clear common limits for duplicated y by previous limits
    listLimits[](xbit, ybit)
    xPrevLimitList[](xbit, ybit)
    """
    print(' check_prev_y_in_limits')
    print_list_xy('listLimits-s',listLimits)
    print_list_xy('xPrevLimitList',xPrevLimitList)
    lenP = len(xPrevLimitList)
    lenL = len(listLimits)
    
    for kp in range(lenP):
        yp = xPrevLimitList[kp][1]
        for kl in range(lenL):
            if listLimits[kl][1] == yp:
                listLimits[kl] = EMPTY_TURTLE
                
    print_list_xy('listLimits-e',listLimits)            

#   clear limits                
    for kl in range (lenL-1, -1, -1):
        if listLimits[kl] == EMPTY_TURTLE:
            del listLimits[kl]
        
    print_list_xy('listLimits-f',listLimits)    
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
    print(' create_limits')
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
            
#            maxToClear = len(listLimits)
            
            connection=get_x_connection(indx, binMG)
            listLimits.append(connection)
            
            if len(xPrevLimitList) > 0 :
 #               maxToClear += 1 #= len(listLimits)
                xPrevLimitList = add_xy_bits_to_prev(indx,binMG,xPrevLimitList)
                xPrevLimitList = check_dupl_y_in_prev(xPrevLimitList)
                listLimits = check_prev_y_in_limits(listLimits,xPrevLimitList)
                listLimits.extend(xPrevLimitList)
                print_list_xy('after extend listLimits',listLimits)
                #!listLimits = check_previous_y(maxToClear,listLimits) + y dupl del by reverse
    
    return listLimits

#-------------------
#-------------------

graph = graph_input(fileName)
print(' after graph_input')
print('graph:',graph)
    
lX, lY = gen_edjes(graph)
nX=len(lX)
nY=len(lY)
print('lX:',nX,lX)
print('lY:',nY,lY)

matrG = [ [0]*nY for i in range(nX) ]

for keyX in graph.keys():
    indX=lX.index(keyX)
    for y in graph[keyX]:
        indY=lY.index(y)
        matrG[indX][indY] = 1

print('matrG:',matrG)

binMG, lX, matrG = sort_y_matrG(matrG, lX)
print(' after sort_y_matrG')
print('binMG:',binMG)
print('lX:',nX,lX)
print('matrG:',matrG)

npMG = np.array(matrG)
#print('npMG:',npMG)
#print('npMGT',npMG.transpose())
npMX = np.dot(npMG, npMG.transpose())
print(' after np.dot')
print('npMX:',npMX)
npQX = np.minimum(npMX, 1)
print('npQX:',npQX)

listR = []

listR = create_limits(npQX, binMG)
print_list_xy('listR',listR)

#!print_limits(listR)

