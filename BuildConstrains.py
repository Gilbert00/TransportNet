import os
import sys, getopt
import datetime #as dt
import queue
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
#--------
class Constants:
    EMPTY_EL=-1
    EMPTY_TURTLE=(EMPTY_EL,EMPTY_EL)
    TST=0
    
#--------
#--------
class Edge:
    def __init__(self):
        self.e:list = []
        self.n:int = 0
        
    def append(self, v):
        self.e.append(v)
        
    def len(self):
        self.n = len(self.e)
        return self.n

#--------        
#--------
class Graph:
    def __init__(self):
        self.dual:logical = False
        self.lX:list = []
        self.lY:list = []
        self.nX = 0
        self.nY = 0
        self.gr:dict = dict()
        
    def get_dual(self):
        return self.dual
        
#-------- 
# Graph
    def input(self, fileName):

        with open(fileName, mode ='r') as csv_file:
            fileReader = csv.reader(csv_file)
            for row in fileReader:
     #           print('row',row)
                if len(row)==0: continue
                self.gr[row[0]] = [row[i] for i in range(1, len(row))]

     #   print()
     #   print(graph)

#--------
# Graph
    def gen_edges(self):
        # geX=[]
        # geY=[]
        
     #   print('graph_ge',graph)
        for keyX in self.gr.keys():
    #        print('keyX',keyX)
            if not check_list(self.lX, keyX):
                self.lX.append(keyX)
            
            for y in self.gr[keyX]:
    #            print('y',y)
                if not check_list(self.lY, y):
                    self.lY.append(y)
        
    #    print('geX:',geX)
    #    print('geY:',geY)

#--------
# Graph
    def create_dual(self):
        graph_dual = Graph()
     
        NON=-1
        
        for keyX in self.gr.keys(): # new y
     #Tst        print('keyX',keyX)
            
            for y in self.gr[keyX]: # new x
     #Tst            print('y',y)
                if graph_dual.gr.get(y,NON) == NON :
     #Tst                print('NON')
                    graph_dual.gr[y] = []
                    graph_dual.gr[y].append(keyX)
                else:
                    if not check_list(graph_dual.gr[y], keyX):
     #Tst                    print('append')
                        graph_dual.gr[y].append(keyX)
                    else:
                        pass #Error! Dupl edje
            
     #Tst2   print('graph_dual:',graph_dual.gr)
        for key in graph_dual.gr.keys():
            graph_dual.gr[key].sort()
            
        graph_dual.dual = True

    #    print('graph_dual:',graph_dual.gr)
        return graph_dual

#--------
# Graph
    def build_net_limits(self):
        """
        graph: 
        """
    #Tst    print('dual:',self.dual)

        self.gen_edges()
        nX = len(self.lX)
        nY = len(self.lY)
        if Constants.TST in [1]: print('lX:',nX,self.lX)
        if Constants.TST in [1]: print('lY:',nY,self.lY)

        matrG:MatrG = self.set_matrG()

        if Constants.TST in [1]: print('matrG:',matrG)
        npQX = QX(matrG)
        #print('npQX:',npQX.npQX)   #Tst
        if Constants.TST in [1,4]: print('npQX:',npQX)
        matrG, self.lX, binMG = matrG.sort_matrG_by_x(self.lX, self.lY, npQX)
        if Constants.TST in [1,4]: print(' after sort_matrG_by_x')#Tst1
        if Constants.TST in [1,4]: print('matrG:',matrG)#Tst1
        if Constants.TST in [1,4]: print('lX:',nX,lX)#Tst1
        if Constants.TST in [1,4]: print('binMG:',binMG)#Tst1

        npQX = QX(matrG)
        if Constants.TST in [1,4]: print('npQX:',npQX)#Tst

        #listR = Limits()

        listR = Limits.create_limits(npQX, binMG)
        listR.print_list_xy('listR')

        listR.print_limits(self)

#--------
# Graph
    def set_matrG(self): #, nX, nY):
        matr = MatrG(len(self.lX),len(self.lY)) #[ [0]*nY for i in range(nX) ]
    #   matrG: [indX][indY] -> 1 if x connect y
        for keyX in self.gr.keys():
            indX=self.lX.index(keyX)
            for y in self.gr[keyX]:
                indY=self.lY.index(y)
                matr.set_el(indX, indY, 1) #matr.matrG[indX][indY] = 1
                
        return matr
        
#--------
#--------
class MatrG(list):
    def __init__(self, nX, nY):
        self.matrG = [ [0]*nY for i in range(nX) ] 
        
    def get_matr(self):
        return self.matrG

    def set_binY(self,iv):
        binY=0
        for ky in range(len(self.matrG[iv])):
            if self.matrG[iv][ky] == 1:
                binY += xbit(ky)
                
        return binY
        
    def get_row(self, kl):
        return self.matrG[kl]
        
    def set_row(self, kl, low):
        self.matrG[kl] = low
        
    def set_el(self, ix, iy, val): 
        self.matrG[ix][iy] = val
        
    def sort_matrG_by_x(self, lX, lY, npQX):
        """
        matrG[x][y]
        lX[x]
        lY[y]
        -
        inbinMG[(binY,kx)],inlX[],inmatrMG[][]: binMG, lX, matrG
        """
        nX = len(lX)
        nY = len(lY)
        if Constants.TST in [1]: print(' sort_matrG_by_x')
    #    inbinMG=[]
        if Constants.TST in [1]: print('matrG:',matrG)
        if Constants.TST in [1]: print('lX:',nX,lX)
        if Constants.TST in [1]: print('lY:',nY,lY)
        if Constants.TST in [1]: print('npQX:',npQX)
        #print('npQX:',npQX.npQX)   #Tst   
        inbinMG:BinMG = npQX.sort_BSF_x(self)
        if Constants.TST in [1]: print('inbinMG:',inbinMG)
        

    # sort lX, matrG  
        inlX = [-1 for i in range(nX) ]
        inmatrG = MatrG(nX,nY) #[ [0]*nY for i in range(nX) ]  
        for kx in range(inbinMG.len()):
           kxOld = inbinMG.get_kxOld(kx)
           inlX[kx] = lX[kxOld] 
           inmatrG.set_row(kx, self.get_row(kxOld))

        if Constants.TST in [1]: print('inlX:',inlX)
        if Constants.TST in [1]: print('inmatrMG:',inmatrG)
        return inmatrG, inlX, inbinMG

#--------
#--------
class QX:
    def __init__(self, matrG):
        matr = matrG.get_matr()
        self.npQX = QX.set_npQX(matr)
        
    def set_npQX(matr):
        npMG = np.array(matr)
        #Tst    print('npMG:',npMG)
        npMX = np.dot(npMG, npMG.transpose())
        #Tst print(' after np.dot')
        #Tst print('npMX:',npMX)
        npQX = np.minimum(npMX, 1)

        return npQX
        
    def get_el(self,i,j):  
        return self.npQX[i][j]
        
    def len(self):
        return len(self.npQX)
    
    def sort_BSF_x(self, matrG:MatrG):
# BFS
# Вход: граф G = (V, E), представленный в виде списков смежности,
# и вершина s из V.
# Постусловие: вершина достижима из s тогда и только тогда,
# когда она помечена как «разведанная».
# 1) пометить s как разведанную вершину, все остальные как не-
# разведанные
# 2) Q := очередь, инициализированная вершиной s
# 3) while Q не является пустой do
# 4) удалить вершину из начала Q, назвать ее v
# 5) for каждое ребро (v, w) в списке смежности v do
# 6) if w не разведана then
# 7) пометить w как разведанную
# 8) добавить w в конец Q  
        """
        inbinMG[(binY,kx)]
        """

        q = queue.Queue()
        #print('self.npQX',self.npQX) #Tst
        #print('matrG:',matrG.matrG) #Tst
        lenQX = len(self.npQX)
        explored = [ False for i in range(lenQX) ]
        inbinMG = BinMG(lenQX) #[ (-1,-1) for i in range(lenQX) ]
        
        ibin: int = 0
        while ibin < lenQX:
            istrt = ibin
            explored[istrt] = True
            q.put(istrt)
            while not q.empty():
                iv = q.get()
                inbinMG.set_row(ibin, (matrG.set_binY(iv),iv))
                ibin += 1
                for iw in range(len(self.npQX[iv])):
                    if (self.npQX[iv][iw] == 1) and (not explored[iw]):
                        explored[iw] = True
                        q.put(iw)

        return inbinMG
        
#--------
#--------
class BinMG:
    def __init__(self, ln):
        self.binMG = [ (-1,-1) for i in range(ln) ]
        
    def get_kxOld(self, kx):
        return self.binMG[kx][1]
        
    def get_y(self, kx):
        return self.binMG[kx][0]
        
    def set_row(self, kx, val):
        self.binMG[kx] = val
        
    def get_row(self, kx):
        return self.binMG[kx]
        
    def len(self):
        return len(self.binMG)
#---------- 
# BinMG
    def get_x_connection(self, indx):
        """
        binMG[(bitY,kx)]
        return (xbit, ybit)
        """
        x=xbit(indx)
        y=self.get_y(indx)
        if Constants.TST in [4]: print(' indx,x,y current:',indx,int2BinStr(x),int2BinStr(y))#Constants.TST4
        return ((x, y))
#---------- 
# BinMG
    def get_sorted_prev_connections(self,indx,npQX):
        """
        It get previous connected vertexes  for current vertex 
        indx: int
        npQX[kx][kx]
        binMG[(binY,kx)]
        -
        prev_connections[](xbit, ybit)
        """
        if Constants.TST in [4]: print(' get_sorted_prev_connections')#Tst
    #Tst     print(f'npQX[{indx}]',npQX[indx])
        prev_connections=Limits()
        
        for i in range(indx):
            if npQX.get_el(indx,i) == 1:
                prev_connections.append((xbit(i), self.get_y(i)))  # get_x_connection
        
        if Constants.TST in [4]: print('indx,prev_connections',indx,prev_connections.get_list())#Tst
        return prev_connections

#--------
#--------         
class Limit:
    pass

class Limits:
    def __init__(self):
        self.limits = []
        
    def append(self, limit):
        self.limits.append(limit)
        
    def extend(self, limits):
        self.limits.extend(limits)

    def delete(self, krow):
        del self.limits[krow]
        
    def set_row(self, krow, val):
        self.limits[krow] = val
        
    def get_row(self, krow):
        return self.limits[krow]    
     
    def get_x(self, krow):
        return self.limits[krow][0]
        
    def get_y(self, krow):
        return self.limits[krow][1]

    def len(self):
        return len(self.limits)
        
    def get_list(self):
        return self.limits
#----------  
# cls
    def create_limits(npQX, binMG):
        """
        npQX[kx][kx]
        binMG[](bitY,kx)
        -
        listLimits[(xbit, ybit)]
        xPrevConnections[(xbit, ybit)]
        connection (xbit, ybit)
        xPrevLimitList[(xbit, ybit)]
        """
    #Tst     print(' create_limits')
        listLimits = Limits()
        for indx in range(npQX.len()):
            if Constants.TST in [4]: print('indQX:',indx)
            if indx == 0 :
                connection = binMG.get_x_connection(indx)
                listLimits.append(connection)
                if Constants.TST in [4]: listLimits.print_list_xy('after append listLimits')
                continue
            else:
                xPrevConnections = binMG.get_sorted_prev_connections(indx,npQX)
                if xPrevConnections.len() > 0 :
                    xPrevLimitList = listLimits.get_prev_connected_limits(xPrevConnections)
                else: # First vertex in connected component
                    xPrevLimitList = Limits()
                
                connection = binMG.get_x_connection(indx)
                listLimits.append(connection)
                if Constants.TST in [4]: listLimits.print_list_xy('after append listLimits')
                
                if xPrevLimitList.len() > 0 :
                    xPrevLimitList.add_xy_bits_to_prev(indx,binMG)
                    xPrevLimitList.check_dupl_y_in_prev()
                    listLimits.check_prev_y_in_limits(xPrevLimitList)
                    listLimits.extend(xPrevLimitList.get_list())
                    if Constants.TST in [4]: listLimits.print_list_xy('after extend listLimits')
                        
        return listLimits

#----------  
# Limits
    def get_prev_connected_limits(self, xPrevConnections):
        """
        for each prev connected vertex we get limits with it
        listLimits[](xbit, ybit)
        xPrevConnections[](xbit, ybit)
        -
        prev_limits[](xbit, ybit)
        """
        if Constants.TST in [4]: print(' get_prev_connected_limits')#Tst
    #Tst     xPrevConnections.print_list_xy('xPrevConnections')
    #Tst     listLimits.print_list_xy('listLimits')
        
        prev_limits=Limits()
        for i in range(xPrevConnections.len()) :
            xPrev = xPrevConnections.get_x(i)
    #Tst         print('i,xPrev:',i,xPrev)
            for k in range(self.len()) :
                if self.get_x(k) & xPrev > 0 :
                    prev_limits.append(self.get_row(k))
              
        if Constants.TST in [4]: prev_limits.print_list_xy('prev_limits')#Tst
        return prev_limits
#----------
# Limits
    def add_xy_bits_to_prev(self,indx,binMG):
        """
        adding current x & y to each previous limit
        binMG[(bitY,kx)]
        xPrevLimitList[(xbit, ybit)]
        """
        if Constants.TST in [4]: print(' add_xy_bits_to_prev')#Tst
        if Constants.TST in [4]: self.print_list_xy('xPrevLimitList-s')#Tst
        x=xbit(indx)
        y=binMG.get_y(indx)
        if Constants.TST in [4]: print('indx,x,y:',indx,int2BinStr(x),int2BinStr(y))#Tst
        
        for i in range(self.len()):
            self.limits[i] = (self.get_x(i) | x, self.get_y(i) | y) 
        
        if Constants.TST in [4]: self.print_list_xy('xPrevLimitList-f')#Tst
    #    return xPrevLimitList
#----------
# Limits
    def check_dupl_y_in_prev(self):
        """
        check and clear previous limits for duplicated y
        xPrevLimitList[(xbit, ybit)]
        """
        if Constants.TST in [4]: print(' check_dupl_y_in_prev')#Tst
        if Constants.TST in [4]: self.print_list_xy('xPrevLimitList-s')#Tst
        lenP = self.len()
        
        for kp in range(lenP-1, -1, -1):
            yp = self.get_y(kp)
            if yp == Constants.EMPTY_EL: continue
            for kl in range(kp-1, -1, -1):
                if self.get_y(kl) == yp:
                    self.set_row(kp, (self.get_x(kp) | self.get_x(kl), yp)) 
                    self.set_row(kl, Constants.EMPTY_TURTLE)    

    #!!!check_dupl_xy(xPrevLimitList)
    #   clear list
        
        if Constants.TST in [4]: self.print_list_xy('xPrevLimitList-e')#Tst
        
    #   clear previous limits
    #   clear list 
        for kp in range (lenP-1, -1, -1):
            if self.get_row(kp) == Constants.EMPTY_TURTLE:
                self.delete(kp)
                
        if Constants.TST in [4]: self.print_list_xy('xPrevLimitList-f')#Tst
#        return xPrevLimitList
#----------
# Limits
    def check_prev_y_in_limits(self,xPrevLimitList):
        """
        check and clear common limits for duplicated y by previous limits
        listLimits[(xbit, ybit)]
        xPrevLimitList[(xbit, ybit)]
        """
        if Constants.TST in [4]: print(' check_prev_y_in_limits')#Tst
        if Constants.TST in [4]: xPrevLimitList.print_list_xy('xPrevLimitList')#Tst
        if Constants.TST in [4]: self.print_list_xy('listLimits-s')#Tst+
        lenP = xPrevLimitList.len()
        lenL = self.len()
        
        for kp in range(lenP):
            yp = xPrevLimitList.get_y(kp)
            for kl in range(lenL):
                if self.get_y(kl) == yp:
                    xPrevLimitList.set_row(kp, (xPrevLimitList.get_x(kp) | self.get_x(kl), yp)) 
                    self.set_row(kl, Constants.EMPTY_TURTLE)

    #!!!check_dupl_xy(xPrevLimitList)
                    
        if Constants.TST in [4]: self.print_list_xy('listLimits-e')#Tst            

    #   clear limits 
    #   clear list               
        for kl in range (lenL-1, -1, -1):
            if self.get_row(kl) == Constants.EMPTY_TURTLE:
                self.delete(kl)
            
        if Constants.TST in [4]: self.print_list_xy('listLimits-f')  #Tst  
    #    return listLimits
#----------
# Limits    
    def print_limits(self, graph):
        """
        print limit in symbolic view
        listR[(xbit, ybit)]
        lX[x]
        lY[y]
        dual: logical
        -
        lXsort[[val, oldInd]]
        lYsort[[val, oldInd]]
        """
            
        lXsort = sort_list_by_val(graph.lX)
        lYsort = sort_list_by_val(graph.lY)
        
 ##       for l in listR:
        for iR in range(self.len()):
#            l = self.get_row(iR)
    #Tst3        print('R:',l)
            sl,sr = '',''
            # xa = int2BinStr(l[0])
            # yb = int2BinStr(l[1])
            xa = int2BinStr(self.get_x(iR))
            yb = int2BinStr(self.get_y(iR))
 #!!!           xa,yb = int2BinStr(self.get_row(iR))
    #Tst3        print('xa,yb:',xa,yb)
     
            if graph.get_dual():
                sl = edge_to_limit_side('b',xa,lXsort)
                sr = edge_to_limit_side('a',yb,lYsort)
            else:
                sl = edge_to_limit_side('a',xa,lXsort)
                sr = edge_to_limit_side('b',yb,lYsort)
            
            print(sl,'<=',sr)

#----------
# Limits  
    def print_list_xy(self, prefix):
        """
        prefix: str
        list_xy[](xbin, ybin)
        """
        
        list_xy = self.get_list()
        print(prefix+':', list_xy)
     
        lp=[]
        for i in range(len(list_xy)):
            lp.append( (int2BinStr(list_xy[i][0]),int2BinStr(list_xy[i][1])) )
        print(' '*len(prefix), lp)
        
#-------------------
#-------------------
def check_list(l, e):
#    print('list',l)
#    print('e',e)
    for k in l:
        if len(l)>0 and k==e: return True
    
    return False    

#-------------------
def int2BinStr(i):
    return bin(i)[2:]
    
#-------------------
def xbit(indx):
    return 2**indx # 1<<indx

#-------------------
def get_lineIndx_by_val_in_col(matr,col,e):
    for k in range(len(matr)):
        if matr[k][col]==e: return k
    
    return -1  
    
#----------
def edge_to_limit_side(prefix,bitStr,edgeSorted):
    """
    prefix str
    bitStr
    edgeSorted[[vertex, vertexIndxInLimit]]
    """
   
    listBit = list(bitStr)
    listBit.reverse()
#Tst3    print('listBit:',listBit)
#Tst3    print('edge:',edgeSorted)
    
    n = len(listBit)
    listS = []
    for i in range(n):
        if listBit[i] == '1' :
            j=get_lineIndx_by_val_in_col(edgeSorted,1,i)
            listS.append(edgeSorted[j][0])
            
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

#----------
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

#----------
def main(argv):

    fileName = argv[1]
    print(fileName)
    
# check filename for existence !  
    if not os.path.isfile(fileName):
        print(f"The input file {fileName} doesn't exists!")
        return
  
    if len(sys.argv) > 2 :
        nMode = int(argv[2])
    else:
        nMode = 0

    print('mode:',nMode)
    graph = Graph()

    graph.input(fileName)
    #Tst print(' after graph_input')
    print('graph:',graph.gr)

    if nMode in (0,1):
    #    dual = False
        graph.build_net_limits()

    if nMode in (0,2):
    #    dual = True
        graph_dual = graph.create_dual()
        print('graph_dual:',graph_dual.gr)
        graph_dual.build_net_limits()

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
