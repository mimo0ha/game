import copy
import math
import random

def red(skk): return "\033[91m {}\033[00m" .format(skk)
def green(skk): return "\033[92m {}\033[00m" .format(skk)
def yellow(skk): return "\033[93m {}\033[00m" .format(skk)
def lightPurple(skk): return "\033[94m {}\033[00m" .format(skk)
def purple(skk): return "\033[95m {}\033[00m" .format(skk)
def cyan(skk): return "\033[96m {}\033[00m" .format(skk)
def lightGray(skk): return "\033[97m {}\033[00m" .format(skk)
def black(skk): return "\033[98m {}\033[00m" .format(skk)

class GameState:

    def __init__(self, eval=0):
        self.board = [[-1, -1, -1, -1], [-1, -1, -1, -1]]
        self.eval = eval

    def printGame(self):
        print(cyan('Human game :\n'), cyan(self.board[0]))
        print(red('Computer game :\n'), red(self.board[1]))

    def __hash__(self):
        return hash(tuple(map(tuple, self.board)))

    def __eq__(self, other):
        return isinstance(other, GameState) and self.board == other.board

class Move:

    def __init__(self, numberOfMoves, probability, id=0, hasKhal=False, changeTurn=False):
        self.num = numberOfMoves
        self.prob = probability
        self.hasKhal = hasKhal
        self.changeTurn = changeTurn
        self.id = id

    def print(self):
        print(self.num, self.prob, self.hasKhal, self.changeTurn)

    def __eq__(self, other):
        return isinstance(other, GameState) and self.id == other.id

    def __gt__(self, other):
        return self.id >= other.id

class ListOfMoves:
    probability = 1 
    def __init__(self, moves):
        self.moves = moves
        for i in self.moves:
            self.probability *= i.prob

    def __hash__(self):
        return hash(map(tuple, self.moves))

    def __eq__(self, other):
        return isinstance(other, ListOfMoves) and self.moves == other.moves

class GameLogic:
    bath = 83
    depth = 4
    availableSequentialMovesNum =2
    # shell faces Probabilities
    upProbability = 0.60
    downProbability = 0.40
    # places where the soldiers will have safety in it
    protected = [10, 21, 27, 38, 44, 55, 61, 72]

    def __init__(self):
        self.moves = [
            Move(10, self.prob(5), True),
            Move(24, self.prob(1), True),
            Move(6, self.prob(6),),
            Move(12, self.prob(0)),
            Move(4, self.prob(2), changeTurn=True),
            Move(3, self.prob(3), changeTurn=True),
            Move(2, self.prob(4), changeTurn=True),
        ]
        for i in range(7):
            self.moves[i].id = i
    
        self.availableMoves= self.getAvailableMoves(0,None)
        for i in self.availableMoves:
             i.sort()
        self.availableMoves = [ListOfMoves(i) for i in self.availableMoves]
        self.availableMoves = list(set(self.availableMoves))
        
    # will return the probability of the move in rule nCr * p^n * (p-1)^n-r
    def prob(self, up):
        return math.comb(6, up) * (self.upProbability**up) * (self.downProbability**(6 - up))
    # one player have to drive his soldiers to kitchen (which is in place number 82)

    def isFinalState(state: GameState):
        yes1 = True
        yes2 = True
        for i in state.board[0]:
            if i != 82:
                yes1 = False
        for i in state.board[1]:
            if i != 82:
                yes2 = False
        return yes2 or yes1
    
    def isInitialState(self,state:GameState ,turn ): 
        for i in state.board[turn]:
            if i != -1  and i != 82:
                return False
        return True
    
    def isValid(self, state: GameState, move: Move, turn, numOfStone, numberOfOptionalStone=None):

        if state.board[turn][numOfStone] == -1 and numOfStone != numberOfOptionalStone:
            return False

        num = move.num
        if numOfStone == numberOfOptionalStone:
            num += 1

        if state.board[turn][numOfStone] + num > self.bath:
            return False

        for i in state.board[1-turn]:
            if i in self.protected:
                first = state.board[turn][numOfStone] + num
                second = i
                if max(first, second) % (self.bath//2 - 7) ==  min(first, second):
                    return False

        return True
    
    def createNextState(self, state: GameState, move: Move, turn, numOfStone, numberOfOptionalStone=None):

        newState = copy.deepcopy(state)
        newState.board[turn][numOfStone] += move.num

        if numberOfOptionalStone != None:
            newState.board[turn][numberOfOptionalStone] +=1

        for i in range(4):
            first = newState.board[turn][numOfStone]
            second = state.board[1-turn][i]
            if first <= self.bath//2 and second <= self.bath//2 or first >= self.bath//2 and second >= self.bath//2 or first<=6 or second<=6  or first >=75 or second>=75:
                continue
            if max(first, second) % (self.bath//2-7) == min(first,  second):
                newState.board[1-turn][i] = -1
                print('kill')

        return newState

    def getAllNextStates(self, state: GameState, move: Move, turn):
        states = []
        if move.hasKhal:
            for i in range(4):
                for j in range(4):
                    if self.isValid(state, move, turn, i, j):
                        states.append(self.createNextState(
                            state, move, turn, i, j))
        else:
            for i in range(4):
                if self.isValid(state, move, turn, i):
                    states.append(self.createNextState(
                        state, move, turn, i))

        return states

    def getAvailableMoves(self, numberOfMoves, move):
        if (move != None):
            if (self.availableSequentialMovesNum == numberOfMoves or move.changeTurn):
                return [[move]]
        ans = []
        for i in self.moves:
            for j in self.getAvailableMoves(numberOfMoves+1, i):
                temp = []
                if (move != None):
                    temp = [move]
                temp.extend(j)
                ans.append(temp)
        return ans

    def getRandomMove(self):
        probabilities = [p.prob for p in self.moves]
        total_probabilities = sum(probabilities)
        probabilities = [p / total_probabilities for p in probabilities]
        move = random.choices(self.moves, probabilities)[0]
        return move

    def play(self):
        moves = []
        moves.append(self.getRandomMove())
        while not moves[len(moves)-1].changeTurn:
            moves.append(self.getRandomMove())
        self.playerMoves = moves

    def computerPlay(self, state, moveNumber):
        if moveNumber == len(self.playerMoves):
            return [state]
        states = self.getAllNextStates(state, self.playerMoves[moveNumber], 1)

        for i in states:
            i.board[0].sort()
            i.board[1].sort()

        temp = set(states)
        states = list(temp)

        ans = []
        for i in states:
            ans.extend(self.computerPlay(i, moveNumber+1))

        for i in ans:
            i.board[0].sort()
            i.board[1].sort()

        temp = set(ans)
        ans = list(temp)

        return ans

    def userPlay(self, state, moveNumber):
        if moveNumber == 0:
            print('You have', len(self.playerMoves), 'move:')
            for i in self.playerMoves:
                i.print()
        state.printGame()
        if moveNumber == len(self.playerMoves) or (self.isInitialState(state,0) and not self.playerMoves[moveNumber].hasKhal):
            return state
        if self.playerMoves[moveNumber].hasKhal:
            print("move number", moveNumber, 'is :')
            self.playerMoves[moveNumber].print()
            print("enter stone index which you want to play the khal on it")
            khal = int(input())
            print("enter stone index which you want to play the main move on it")
            main = int(input())
            if self.isValid(state, self.playerMoves[moveNumber], 0, main, khal):
                return self.userPlay(self.createNextState(state, self.playerMoves[moveNumber], 0, main, khal), moveNumber + 1)
            else:
                print('invalid move !! \ntry again.. ')
                return self.userPlay(state, moveNumber)
        else:
            print("move number", moveNumber, 'is :')
            self.playerMoves[moveNumber].print()
            print("enter stone index which you want to play the main move on it")
            main = int(input())
            if self.isValid(state, self.playerMoves[moveNumber], 0, main):
                return self.userPlay(self.createNextState(state, self.playerMoves[moveNumber], 0, main), moveNumber + 1)
            else:
                print('invalid move !! \ntry again..')
                return self.userPlay(state, moveNumber)

    def eval(self, state, turn):
        e = 0

        for i in state.board[turn]:
            if i == -1:
                e -= 10
            elif i == self.bath:
                e += 25
            else:
                e += i
            if i in self.protected:
                e += 5

        for i in state.board[1-turn]:
            if i == -1:
                e += 10
            elif i == self.bath:
                e -= 25
            else:
                e -= i
            if i in self.protected:
                e -= 5

        return e

    def maxMove(self, state: GameState, depth):
        if depth == self.depth:
            return state
        bestMove = None
        max = -1e10

    def chance(self, state: GameState):
        return state

    def minMove(self, state: GameState, depth):
        if depth == self.depth:
            return state


game = GameState()
logic = GameLogic()

game.board = [[7 , 6    , 3, 2 ], [10, 8, 4, 3]]

# very important to initialize playerMoves list in game logic
logic.play()

# for i in logic.playerMoves: 
#     i.print()
    
# computerNextState =logic.computerPlay(game,0)
# for i in computerNextState:
#     i.printGame()
#     print()

logic.userPlay(game,0)
    
''' if you want to see what are the available list of moves that can be played in the one turn uncomment the following lines'''
# print(len(logic.availableMoves))
# for i in logic.availableMoves : 
#     for j in i.moves : 
#         j.print()
#     print()
