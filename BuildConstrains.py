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
    TST=0 #!!!

#--------
#--------
class Set(int):
    
    def __init__(self, ind=-1):
        if ind == -1:
            self.set:int = 0
        else:
            if not isinstance(ind, int): raise TypeError(f"Type of the {ind} isn't int !")
            if ind < 0: raise ValueError(f"Value of the {ind} is negative ! ")
            
            self.set:int = 1<<ind
            
    def to_int(self):
        return self.set
    
    def __or__(self, other):
        if not isinstance(other, Set): raise TypeError(f"Type of the {other} isn't Set !")
        out = Set()
        out.set = self.set | other.set
        return out

    def __and__(self, other):
        if not isinstance(other, Set): raise TypeError(f"Type of the {other} isn't Set !")
        out = Set()
        out.set = self.set & other.set
        return out

    def __invert__(self):
#            if not isinstance(other, Set): raise TypeError(f"Type of the {other} isn't Set !")
        out = Set()
        out.set = ~self.set
        return out

    def __eq__(self, other):
#            if isinstance(other, int) and other==0:
#                return self.set > other
        if not isinstance(other, Set): raise TypeError(f"Type of the {other} isn't Set !")
        return self.set == other.set

    def __gt__(self, other):
#            if isinstance(other, int) and other==0:
#                return self.set > other
        if not isinstance(other, Set): raise TypeError(f"Type of the {other} isn't Set !")
        return self.set > other.set
    
    # def __ior__(self, other):
        # if not isinstance(other, Set): raise TypeError(f"Type of the {other} isn't Set !")
        # self.set |= other.set        

    def __iand__(self, other):
        if not isinstance(other, Set): raise TypeError(f"Type of the {other} isn't Set !")
        self.set &= other.set

    def set2BinStr(self):   
        return bin(self.set)[2:]
        
    def int2set(i):
        out = Set()
        out.set = i
        return out
        
    def EMPTY_SET():
        return Set()
        
    def EMPTY_TURTLE():
        return ((Set.EMPTY_SET(),Set.EMPTY_SET()))
                        
#--------
#--------
class Matr:
    def __init__(self):
        self.matr:list = []

    def len(self):
        return len(self.matr)
        
    def append(self, v):
        self.matr.append(v) 
        
    def sort(self):
        self.matr.sort()

    def get_lineIndx_by_val_in_col(self,col,e):
        for k in range(len(self.matr)):
            if self.matr[k][col]==e: return k
        
        return -1  
 
    def check_list(l, e):
#       print('list',l)
#       print('e',e)
        n=len(l)
        if n==0 : return False
        for k in l:
            if k==e: return True
        
        return False     
        
#--------
#--------
class Edge(Matr):
    def __init__(self):
        super().__init__()
                
    # def append(self, v):
        # self.super.append(v)
        
    # def len(self):
        # return self.super.len()
        
    def get_vertex(self,k):
        return self.matr[k][0]
        
    def edge_to_limit_side(self,prefix,bitStr):
        """
        prefix str
        bitStr
        edgeSorted[[vertex, vertexIndxInLimit]]
        """
       
        listBit = list(bitStr)
        listBit.reverse()
    #Tst3    print('listBit:',listBit)
    #Tst3    print('edge:',self.matr)
        
        n = len(listBit)
        listS = []
        for i in range(n):
            if listBit[i] == '1' :
                j = self.get_lineIndx_by_val_in_col(1,i)
                listS.append(self.get_vertex(j))
                
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
        
    def sort_list_by_val(lst):

    #Tst3    print(' sort_list_by_val')
        #lst_out: [][lst_el, old_indx]
        lst_out=Edge()
        for i in range(len(lst)):
            lst_out.append([lst[i],i])
        
    #Tst3    print('lst_out-s:',lst_out)
        lst_out.sort()
        
    #Tst3    print('lst_out-f:',lst_out)
        return lst_out


#--------        
#--------
class Graph:
    def __init__(self):
        self.dual:logical = False
        self.lX:list = []
        self.lY:list = []
        # self.nX = 0
        # self.nY = 0
        self.graph:dict = dict()
        
    def get_dual(self):
        return self.dual
        
    def get_graph(self):
        return self.graph
        
#-------- 
# Graph
    def input(self, fileName):

        with open(fileName, mode ='r') as csv_file:
            fileReader = csv.reader(csv_file)
            for row in fileReader:
     #           print('row',row)
                if len(row)==0: continue
                self.graph[row[0]] = [row[i] for i in range(1, len(row))]

     #   print()
     #   print(self.graph)

#--------
# Graph
    def gen_edges(self):
        
     #   print('graph_ge',self.graph)
        for keyX in self.graph.keys():
    #        print('keyX',keyX)
            if not Matr.check_list(self.lX, keyX):
                self.lX.append(keyX)
            
            for y in self.graph[keyX]:
    #            print('y',y)
                if not Matr.check_list(self.lY, y):
                    self.lY.append(y)
        
#--------
# Graph
    def create_dual(self):
        graph_dual = Graph()
     
        NON=-1
        
        for keyX in self.graph.keys(): # new y
     #Tst        print('keyX',keyX)
            
            for y in self.graph[keyX]: # new x
     #Tst            print('y',y)
                if graph_dual.graph.get(y,NON) == NON :
     #Tst                print('NON')
                    graph_dual.graph[y] = []
                    graph_dual.graph[y].append(keyX)
                else:
                    if not Matr.check_list(graph_dual.graph[y], keyX):
     #Tst                    print('append')
                        graph_dual.graph[y].append(keyX)
                    else:
                        pass #Error! Dupl edje
            
     #Tst2   print('graph_dual:',graph_dual.graph)
        for key in graph_dual.graph.keys():
            graph_dual.graph[key].sort()
            
        graph_dual.dual = True

    #    print('graph_dual:',graph_dual.graph)
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

        if Constants.TST in [1]: print('matrG:',matrG.get_matr())
        npQX = QX(matrG)
        #print('npQX:',npQX.npQX)   #Tst
        if Constants.TST in [1,4]: print('npQX:',npQX.get_npQX())
        matrG, self.lX, binMG = matrG.sort_matrG_by_x(self.lX, self.lY, npQX)
        if Constants.TST in [1,4]: print(' after sort_matrG_by_x')#Tst1
        if Constants.TST in [1,4]: print('matrG:',matrG.get_matr())#Tst1
        if Constants.TST in [1,4]: print('lX:',nX,self.lX)#Tst1
        if Constants.TST in [1,4]: print('binMG:',binMG.get_list())#Tst1

        npQX = QX(matrG)
        if Constants.TST in [1,4]: print('npQX:',npQX.get_npQX())#Tst

        listR = Limits.create_limits(npQX, binMG)
        listR.print_list_xy('listR')

        listR.print_limits(self)

#--------
# Graph
    def set_matrG(self): #, nX, nY):
        matr = MatrG(len(self.lX),len(self.lY)) #[ [0]*nY for i in range(nX) ]
    #   matrG: [indX][indY] -> 1 if x connect y
        for keyX in self.graph.keys():
            indX=self.lX.index(keyX)
            for y in self.graph[keyX]:
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
        binY=Set()
#        print('binY',type(binY))
        for ky in range(len(self.matrG[iv])):
            if self.matrG[iv][ky] == 1:
#                print(f'Set({ky})',type(Set(ky)))
                binY = binY | Set(ky)
#                print('ky,binY:',ky,binY.to_int())
#        print('binY:',binY.to_int())
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
        if Constants.TST in [1]: print('matrG:',self.get_matr())
        if Constants.TST in [1]: print('lX:',nX,lX)
        if Constants.TST in [1]: print('lY:',nY,lY)
        if Constants.TST in [1]: print('npQX:',npQX.get_npQX())
        inbinMG:BinMG = npQX.sort_BSF_x(self)
        if Constants.TST in [1]: print('inbinMG:',inbinMG.get_list())
        

    # sort lX, matrG  
        inlX = [-1 for i in range(nX) ]
        inmatrG = MatrG(nX,nY) #[ [0]*nY for i in range(nX) ]  
        for kx in range(inbinMG.len()):
           kxOld = inbinMG.get_kxOld(kx)
           inlX[kx] = lX[kxOld] 
           inmatrG.set_row(kx, self.get_row(kxOld))

        if Constants.TST in [1]: print('inlX:',inlX)
        if Constants.TST in [1]: print('inmatrMG:',inmatrG.get_matr())
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
        
    def get_npQX(self):  
        return self.npQX       
        
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
        #print(' sort_BSF_x')#Tst
        
        q = queue.Queue()
        #print('self.npQX:',self.get_npQX()) #Tst
        #print('matrG:',matrG.get_matr()) #Tst
        lenQX = len(self.npQX)
        explored = [ False for i in range(lenQX) ]
        inbinMG = BinMG(lenQX) #[ (-1,-1) for i in range(lenQX) ]
        #print('inbinMG:',inbinMG.get_list())  #Tst
        
        ibin: int = 0
        while ibin < lenQX:
            istrt = ibin
            explored[istrt] = True
            q.put(istrt)
            while not q.empty():
                iv = q.get()
                y = matrG.set_binY(iv) #Tst
                #print('y:',y.to_int(),y,type(y)) #Tst
                #inbinMG.set_row(ibin, ((matrG.set_binY(iv),iv)))
                inbinMG.set_row(ibin, (y.to_int(),iv)) #Tst
                #inbinMG.set_row(ibin, [y,iv]) #Tst
                ibin += 1
                for iw in range(len(self.npQX[iv])):
                    if (self.npQX[iv][iw] == 1) and (not explored[iw]):
                        explored[iw] = True
                        q.put(iw)

        #print('inbinMG:',inbinMG.get_list())  #Tst
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
        #print('val:',val)
        self.binMG[kx] = val
        
    def get_row(self, kx):
        return self.binMG[kx]
        
    def len(self):
        return len(self.binMG)
        
    def get_list(self):
        return self.binMG
        
#---------- 
# BinMG
    def get_x_connection(self, indx):
        """
        binMG[(bitY,kx)]
        return (xbit, ybit)
        """
        x=Set(indx)
        y=Set.int2set(self.get_y(indx))
        if Constants.TST in [4]: print(' indx,x,y current:',indx,x.set2BinStr(),y.set2BinStr())#Constants.TST4 
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
                prev_connections.append(self.get_x_connection(i)) 
        
        if Constants.TST in [4]: print('indx,prev_connections',indx,prev_connections.out_list())#Tst
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
     
    def get_x(self, krow):  #->Set
        return self.limits[krow][0]
        
    def get_y(self, krow):  #->Set
        return self.limits[krow][1]

    def len(self):
        return len(self.limits)
        
    def get_list(self):
        return self.limits
        
    def out_list(self):    
        list_xy = []
        for i in range(self.len()):
            list_xy.append( ( self.get_x(i).to_int(), self.get_y(i).to_int() )  )    
        return list_xy
        
    def clear_limits(self):
        lenLimits = self.len()
        for k in range (lenLimits-1, -1, -1):
            row = self.get_row(k)
            x = row[0]
            y = row[1]
            if x == Set.EMPTY_SET() and y == Set.EMPTY_SET():
                self.delete(k)
            
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
                if (self.get_x(k) & xPrev).to_int() > 0 :
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
        x,y = binMG.get_x_connection(indx)    
        if Constants.TST in [4]: print('indx,x,y:',indx,x.set2BinStr(),y.set2BinStr())#Tst 
        
        for i in range(self.len()):
            self.set_row(i, (self.get_x(i) | x, self.get_y(i) | y)) 
        
        if Constants.TST in [4]: self.print_list_xy('xPrevLimitList-f')#Tst
    
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
            if yp == Set.EMPTY_SET(): continue
            for kl in range(kp-1, -1, -1):
                if self.get_y(kl) == yp:
                    self.set_row(kp, (self.get_x(kp) | self.get_x(kl), yp)) 
                    self.set_row(kl, Set.EMPTY_TURTLE())    
       
        if Constants.TST in [4]: self.print_list_xy('xPrevLimitList-e')#Tst
        
    #   clear previous limits
    #   clear list 
        self.clear_limits()
                
        if Constants.TST in [4]: self.print_list_xy('xPrevLimitList-f')#Tst

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
                    self.set_row(kl, Set.EMPTY_TURTLE())
                    
        if Constants.TST in [4]: self.print_list_xy('listLimits-e')#Tst            

    #   clear limits 
    #   clear list  
        self.clear_limits()
            
        if Constants.TST in [4]: self.print_list_xy('listLimits-f')  #Tst  
    
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
            
        lXsort = Edge.sort_list_by_val(graph.lX)    
        lYsort = Edge.sort_list_by_val(graph.lY)    
        
 ##       for l in listR:
        for iR in range(self.len()):
            sl,sr = '',''

            xa = self.get_x(iR).set2BinStr()     #
            yb = self.get_y(iR).set2BinStr()     #
 #!!!           xa,yb = set2BinStr(self.get_row(iR))
    #Tst3        print('xa,yb:',xa,yb)
     
            if graph.get_dual():
                sl = lXsort.edge_to_limit_side('b',xa)
                sr = lYsort.edge_to_limit_side('a',yb)
            else:
                sl = lXsort.edge_to_limit_side('a',xa)
                sr = lYsort.edge_to_limit_side('b',yb)
            
            print(sl,'<=',sr)

#----------
# Limits  
    def print_list_xy(self, prefix):
        """
        prefix: str
        list_xy[](xbin, ybin)
        """
        
        list_xy = []
        lp=[]
        for i in range(self.len()):
            list_xy.append( ( self.get_x(i).to_int(), self.get_y(i).to_int() )  )
            lp.append( ( self.get_x(i).set2BinStr(), self.get_y(i).set2BinStr() ) )
        
        print(prefix+':', list_xy)
        print(' '*len(prefix), lp)
        
#-------------------
#-------------------
#-------------------
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
    print('graph:',graph.get_graph())

    if nMode in (0,1):
    #    dual = False
        graph.build_net_limits()

    if nMode in (0,2):
    #    dual = True
        graph_dual = graph.create_dual()
        print('graph_dual:',graph_dual.get_graph())
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
