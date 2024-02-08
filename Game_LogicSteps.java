package mataha;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
public class NodeMataha {
    int n ,m;
    int nowX,nowY;
    int goalX,goalY;
    int [][] state ;
    public NodeMataha(int n,int m, int nowX, int nowY, int goalX, int goalY)
    {
    this.n = n;
    this.m = m;
    this.nowX = nowX;
    this.nowY = nowY;
    this.goalX = goalX;
    this.goalY = goalY;
    this.state = new int[n][m];
    }
    NodeMataha parent;
    int cost ;
    int heuristic ;
    int f = cost + heuristic ;
}
public class BoardMataha {
    public ArrayList<NodeMataha> next_state;
    public ArrayList<NodeMataha> a ;
    public ArrayList<NodeMataha> SolutionList;
    public Stack<NodeMataha> stack;
    public PriorityQueue<NodeMataha> ucsQueue;
    public PriorityQueue<NodeMataha> hill;
    public PriorityQueue<NodeMataha> aStarQueue;
    public Set<String> isVisited  = new HashSet<>();
    public Queue<NodeMataha> queue;
    public int numOfGenNode;
    public int checkNode = 0;
    public int numEdge = 0;
    public int numOfNode = 0;
    
    String Black = "\u001B[30m";
    String White =  "\u001B[37m" ;
    String Green =  "\u001B[32m" ;
    String Yellow =  "\u001B[33m" ;
    String Red =  "\u001B[31m" ;
    String Purple = "\u001B[35m" ;

    Scanner input = new Scanner(System.in);
    
    public NodeMataha initializeBoard(int nowX, int nowY) {
        int n1, m1;//NumOfPointers;
        int goalx,goaly;
        //int [] pointers;
        System.out.println("Enter Num Of Row : ");
        n1 = input.nextInt();
        System.out.println("Enter Num Of Column : ");
        m1 = input.nextInt();
        System.out.println("Enter X of goal : ");
        goalx = input.nextInt();
        System.out.println("Enter Y of goal : ");
        goaly = input.nextInt();
        NodeMataha node1 = new NodeMataha(n1, m1, nowX, nowY, goalx, goaly);
        System.out.println("Paste the n*m array here: ");
        for (int i = 0; i < node1.n; i++) {
            for (int j = 0; j < node1.m; j++) {
                node1.state[i][j] = input.nextInt();
            }
        }
        return node1;
    }

    public void printBoard(NodeMataha node) {
        for (int n = 0; n < node.n; n++) {
            for (int m = 0; m < node.m; m++) {
                if(node.state[n][m]==0){
                System.out.print(Black + node.state[n][m] + " ");
                }
                if(node.state[n][m]==1||node.state[n][m]==11){
                System.out.print(White + node.state[n][m] + " ");
                }
                if(node.state[n][m]==2||node.state[n][m]==12){
                System.out.print(Green + node.state[n][m] + " ");
                }
                if(node.state[n][m]==3||node.state[n][m]==13){
                System.out.print(Yellow + node.state[n][m] + " ");
                }
                if(node.state[n][m]==4||node.state[n][m]==14){
                System.out.print(Red + node.state[n][m] + " ");
                }
                if(node.state[n][m]==5||node.state[n][m]==15){
                System.out.print(Purple + node.state[n][m] + " ");
                }   
            }
            System.out.print("\n");
        }
    }

    public boolean checkMove(NodeMataha node, int nowx, int nowy, char c) {
        boolean valid = false ;
        if (nowx > -1 && nowy > -1 && nowx < node.n && nowy < node.m) {
            if (node.state[nowx][nowy] > 1 && node.state[nowx][nowy] <= 15) {
                if (c == 'w' || c == 's' || c == 'd' || c == 'a') {
                    valid =  true;
                }
            }
        }
        return valid;
    }

    public NodeMataha deepCopy(NodeMataha node, int n, int m, int nowX, int nowY) {
        NodeMataha new_node = new NodeMataha(n, m, nowX, nowY,node.goalX,node.goalY);
        for (int i = 0; i < node.n; i++) {
            for (int j = 0; j < node.m; j++) {
                new_node.state[i][j] = node.state[i][j];
            }
        }
        return new_node;
    }

    public NodeMataha move(NodeMataha node, int nX, int nY, char c) {
        switch (c) {
            case 'w':
                if (checkMove(node, nX - 1, nY, 'w')) {
                    node.state[nX][nY] -= 10;
                    nX = nX - 1;
                    node.nowX = nX;
                    node.state[nX][nY] += 10;
                    //node.state[nX][nY] -= 1;
                } else {
                    System.out.println("Bad Move \n");
                    return null;
                }
                break;
            case 's':
                if (checkMove(node, nX + 1, nY, 's')) {
                    node.state[nX][nY] -= 10;
                    nX = nX + 1;
                    node.nowX = nX;
                    node.state[nX][nY] += 10;
                    //node.state[nX][nY] -= 1;
                } else {
                    System.out.println("Bad Move \n");
                    return null;
                }
                break;
            case 'd':
                if (checkMove(node, nX, nY + 1, 'd')) {
                    node.state[nX][nY] -= 10;
                    nY = nY + 1;
                    node.nowY = nY;
                    node.state[nX][nY] += 10;
                    //.state[nX][nY] -= 1;
                } else {
                    System.out.println("Bad Move \n");
                    return null;
                }
                break;
            case 'a':
                if (checkMove(node, nX, nY - 1, 'a')) {
                    node.state[nX][nY] -= 10;
                    nY = nY - 1;
                    node.nowY = nY;
                    node.state[nX][nY] += 10;
                    //node.state[nX][nY] -= 1;
                } else {
                    System.out.println("Bad Move \n");
                    return null;
                }
                break;
        }
        printBoard(node);
        System.out.println("nowX " + nX + "\n" + "nowY " + nY + "\n");
        System.out.println("********************" + "\n");
        return node;
    }

    public boolean isFinal(NodeMataha node) {
        boolean b = false;
        if (node.nowX == node.goalX && node.nowY == node.goalY)
            {
                b = true ;
            }
        return b;
    }

    public int saveSolution(NodeMataha finalState) {
        int n = 0;
        System.out.println("\n" + " WinPath : ");
        SolutionList = new ArrayList<NodeMataha>();
        SolutionList.add(finalState);
        NodeMataha node = finalState.parent;
        while (node != null) {
            SolutionList.add(node);
            node = node.parent;
        }
        n = SolutionList.size();
        System.out.println("Number Nodes Of Solution = " + n);
        return n;
    }

    public String getHash(NodeMataha node) {
        String str = "";
        for (int i = 0; i < node.n; i++) {
            for (int j = 0; j < node.m; j++) {
                str += node.state[i][j] + "";
            }
            str += "\n";
        }
        return str;
    }
    
    public boolean check(NodeMataha node, int x, int y) {
        boolean valid = false;
        if (x> -1 && y > -1 &&x < node.n && y < node.m) {
            if (node.state[x][y] > 1) {
                valid = true;}
        }
        return valid;
    }

    public ArrayList<NodeMataha> creatNextstate(NodeMataha node) {
        a = new ArrayList<NodeMataha>();
        //char direction[] = {'w', 's', 'd', 'a'};
        if (check(node, node.nowX - 1, node.nowY)) {
            NodeMataha new_node = deepCopy(node, node.n, node.m, node.nowX, node.nowY);
            new_node.state[new_node.nowX][new_node.nowY] -= 10;
            new_node.nowX--;
            new_node.state[new_node.nowX][new_node.nowY] += 10;
            a.add(new_node);
        }
         if (check(node, node.nowX + 1, node.nowY)) {
            NodeMataha new_node = deepCopy(node, node.n, node.m, node.nowX, node.nowY);
            new_node.state[new_node.nowX][new_node.nowY] -= 10;
            new_node.nowX++;
            new_node.state[new_node.nowX][new_node.nowY] += 10;
            a.add(new_node);
        }
         if (check(node, node.nowX , node.nowY+1)) {
            NodeMataha new_node = deepCopy(node, node.n, node.m, node.nowX, node.nowY);
            new_node.state[new_node.nowX][new_node.nowY] -= 10;
            new_node.nowY++;
            new_node.state[new_node.nowX][new_node.nowY] += 10;
            a.add(new_node);
        }
          if (check(node, node.nowX , node.nowY-1)) {
            NodeMataha new_node = deepCopy(node, node.n, node.m, node.nowX, node.nowY);
            new_node.state[new_node.nowX][new_node.nowY] -= 10;
            new_node.nowY--;
            new_node.state[new_node.nowX][new_node.nowY] += 10;
            a.add(new_node);
        } 
        return a;
    }
    
    boolean fo = false ;
    int check = 0;
    
    public NodeMataha aadfs(NodeMataha root){
        check += 1 ;
        //System.out.print("\n" + getHash(root));
        isVisited.add(getHash(root));
        if(fo==true)return null;
        if (isFinal(root)) {
                fo = true ;
                printBoard(root);
                System.out.println(" Check Nood " + check);
                System.out.println( " FINISH ");
                return root;
            }
        for (NodeMataha child : creatNextstate(root)) 
        {
            if(!isVisited.contains(getHash(child)))
            {
            //printBoard(child);
            aadfs(child);
            }
        }
        return null;
    }
    
    public NodeMataha dfs(NodeMataha root) {
        stack = new Stack<NodeMataha>();
        stack.push(root);
        isVisited.add(getHash(root));
        root.parent = null;
        while (!stack.isEmpty()){
            NodeMataha current = stack.pop();
            printBoard(current);
            System.out.println("\n" + "nowX " + current.nowX + "\n" + "nowY " + current.nowY +"\n");
            checkNode++;
            if (isFinal(current)) {
                
                System.out.println("\n" + "check node " + checkNode);
                return current;
            }
            for (NodeMataha child : creatNextstate(current)) {
               if(!isVisited.contains(getHash(child))){
                child.parent = current;
                stack.push(child);
                isVisited.add(getHash(child));
                }
            }
        }
        return null;
    }

    public NodeMataha bfs(NodeMataha root) {
        queue = new LinkedList<NodeMataha>();
        queue.add(root);
        isVisited.add(getHash(root));
        root.parent = null;
        while (!queue.isEmpty()){
            NodeMataha current = queue.remove();
            printBoard(current);
            System.out.println("\n" + "nowX " + current.nowX + "\n" + "nowY " + current.nowY +"\n");
            checkNode++;
            if (isFinal(current)) {
                System.out.println("\n" + "check node " + checkNode);
                return current;
            }
            for (NodeMataha child : creatNextstate(current)) {
                if(!isVisited.contains(getHash(child))){
                child.parent = current;
                queue.add(child);
                isVisited.add(getHash(child));
                }
            }
        }
        return null;
    }
    
    class StateComparator implements Comparator<NodeMataha>{
        @Override
        public int compare(NodeMataha node1, NodeMataha node2) {
            if (node1.cost < node2.cost) {
                return 1;
            }
            else if (node1.cost > node2.cost) {
                return -1;
            }
            return 0;
        }
    }
    
    public NodeMataha ucs(NodeMataha root) {
        ucsQueue = new PriorityQueue <NodeMataha>(new StateComparator());
        root.parent = null;
        root.cost = 0 ;
        ucsQueue.add(root); 
        isVisited.add(getHash(root));
        while (!ucsQueue.isEmpty()) {
            NodeMataha current = ucsQueue.remove();
            checkNode ++ ;
            printBoard(current);
            System.out.println("\n" + "nowX " + current.nowX + "\n" + "nowY " + current.nowY +"\n");
            if (isFinal(current)) {
                System.out.println("Cost Of Path : " + checkNode );
                return current;
            }
            for (NodeMataha child : creatNextstate(current)) {
                if(!isVisited.contains(getHash(child))){
                    child.parent = current;
                    child.cost = current.cost + child.state[child.nowX][child.nowY]; //+1 ;
                    ucsQueue.add(child);
                    isVisited.add(getHash(child));
                }
            }
         }
    return null;
    }

    public int calHeu(NodeMataha node){
        double h;
        int x1 = node.nowX;
        int y1 = node.nowY;
        int x2 = node.goalX;
        int y2 = node.goalY;
        h = Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
        int hi = (int)h;
        return hi ;
    }
    
    public NodeMataha Hill(NodeMataha root){
        hill = new PriorityQueue <NodeMataha>( new Comparator<NodeMataha>(){ 
            @Override
            public int compare(NodeMataha node1, NodeMataha node2) {
                node1.heuristic = calHeu(root) ;
                node2.heuristic = calHeu(root) ;
                if (node1.heuristic  < node2.heuristic) {
                    return 1;
                }
                else if (node1.heuristic  >  node2.heuristic ) {
                    return -1;
                }
                return 0;
            }
        }); 
        root.parent = null;
        root.heuristic = 0 ;
        checkNode = 0;
        hill.add(root);
        isVisited.add(getHash(root));
        while (!hill.isEmpty()) {
            NodeMataha current = hill.remove();
            checkNode ++ ;
            printBoard(current);
            System.out.println("\n" + "nowX " + current.nowX + "\n" + "nowY " + current.nowY +"\n");
            if (isFinal(current)) {
                System.out.println("Cost Of Path : " + checkNode );
                return current;
            }
            for (NodeMataha child : creatNextstate(current)) {
                if(!isVisited.contains(getHash(child))){
                    child.parent = current;
                    child.heuristic = calHeu(child) + calHeu(current);
                    hill.add(child);
                    isVisited.add(getHash(child));
                }
            }
         }
    return null;
    }
    
    public NodeMataha aStar(NodeMataha root){
        aStarQueue = new PriorityQueue <NodeMataha>( new Comparator<NodeMataha>(){ 
            @Override
            public int compare(NodeMataha node1, NodeMataha node2) {
                node1.heuristic = calHeu(root) ;
                node2.heuristic = calHeu(root) ;
                if (node1.heuristic + node1.cost < node2.heuristic+ node2.cost) {
                    return 1;
                }
                else if (node1.heuristic + node1.cost >  node2.heuristic+ node2.cost) {
                    return -1;
                }
                return 0;
            }
        });
        root.parent = null;
        root.cost = 0 ;
        checkNode = 0;
        aStarQueue.add(root);
        isVisited.add(getHash(root));
        while (!aStarQueue.isEmpty()) {
            NodeMataha current = aStarQueue.remove();
            checkNode ++ ;
            printBoard(current);
            System.out.println("\n" + "nowX " + current.nowX + "\n" + "nowY " + current.nowY +"\n");
            if (isFinal(current)) {
                System.out.println("Cost Of Path : " + checkNode );
                return current;
            }
            for (NodeMataha child : creatNextstate(current)) {
                if(!isVisited.contains(getHash(child))){
                    child.parent = current;
                    child.cost = current.cost + child.state[child.nowX][child.nowY]; //+1 ;
                    child.heuristic = calHeu(child);
                    child.f = child.cost + child.heuristic;
                    aStarQueue.add(child);
                    isVisited.add(getHash(child));
                }
            }
         }
    return null;
    }

}
public class Mataha {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
      BoardMataha board = new BoardMataha();
      int x=1 , y=3 ;
      NodeMataha n = board.initializeBoard(x,y);
      boolean ff = true ;
      do {
         System.out.println( "\n" +"Enter strategy you want to  play with : " + "\n" +
                                " 1) User Game . "  + "\n" +
                                " 2) DFS . " + "\n" +
                                " 3) BFS . " + "\n" + 
                                " 4) UCS . " + "\n" + 
                                " 5) HILL . " + "\n" + 
                                " 6) A*  . " + "\n" +
                                " 7) DFS AA  . " + "\n" +
                                " 8) Exit . "                  
                                ) ;
         int typeOfGame = input.nextInt();
         switch(typeOfGame)
        {
            case 1 :
                do {
                    System.out.println("-------------------- \n"
                    + " W to go Up \n"
                    + " S to go Down \n"
                    + " D to go Right \n"
                    + " A to go Left \n");
                    char c ;
                    c = input.next().charAt(0);    
                    board.move(n,n.nowX,n.nowY,c);
                    //board.creatNextstate(n);
                }while( ! board.isFinal(n));
                System.out.println(".....YOU Win.....");
                break;
            case 2 :
            {
                //Node node = n;
                System.out.println(" DFS Solution : ") ;
                board.dfs(n) ;
                System.out.println(" DFS finish  ") ;
                break;
            }
            case 3 :
            {
                //Node node = n;
                System.out.println(" BFS Solution : ") ;
                board.bfs(n);
                System.out.println(" BFS finish  ") ;
                break;
            }
            case 4 :
            {
                System.out.println(" UCS Solution : ") ;
                board.ucs(n);
                System.out.println(" UCS finish  ") ;
                break;
            }
            case 5 :
            {
                System.out.println(" HILL Solution : ") ;
                board.Hill(n);
                System.out.println(" HILL finish  ") ;
                break;
            }
            case 6 :
            {
                System.out.println(" A star Solution : ") ;
                board.aStar(n);
                System.out.println(" A star finish  ") ;
                break;
            }
            case 7 :
            {
                System.out.println(" DFS AA Solution : ") ;
                board.aadfs(n);
                System.out.println(" DFS AA finish  ") ;
                break;
            }
            default :{
                    ff=false ;
                    System.out.println("<<---Thank You for Play --->>");
                }
                input.close();
            }
        }while(ff);
        
    }
    
}
