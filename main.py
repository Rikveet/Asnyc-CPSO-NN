import math
import random
from termcolor import colored

maxW = 0.5
minW = -0.5
Vmax = 0.9
W = 0.729844
c1 = 1.49618  # 1.49618
c2 = 1.49618
nSubParticles = 25
bestError = float("inf")
structure = {}
activationFunctions = {}
tActivation = ""
inputData = []
contextVec = []
CPSO = []
show = False


def load_data(t):
    global inputData
    if t == "iris":
        f = open("Data/Iris/iris.data")
        classification = {
            "Iris-setosa": 1,
            "Iris-versicolor": 2,
            "Iris-virginica": 3
        }
        fs = f.read().strip().split("\n")
        for i in range(len(fs)):
            temp = fs[i].split(",")
            expectedOutputLayer = []
            for key in classification.keys():
                if key == temp[4]:
                    expectedOutputLayer.append(1)
                else:
                    expectedOutputLayer.append(0)
            tempTof = []
            for j in range(len(temp) - 1):
                tempTof.append(float(temp[j]))
            tempTof.append(expectedOutputLayer)
            inputData.append(tempTof)
            random.shuffle(inputData)


class Particle:
    def __init__(self, w):
        self.velocities = [0 for _ in range(len(w))]
        self.weights = w
        self.bValue = float("inf")
        self.bWeights = [float("inf") for _ in range(len(w))]


def sigmoid(inpt):
    return 1 / (1 + math.exp(-inpt))


def tanh(inpt):
    return (math.exp(inpt) - math.exp(-inpt)) / (math.exp(inpt) + math.exp(-inpt))


def activation_function(inpt):
    global tActivation, activationFunctions
    try:
        return activationFunctions[tActivation](inpt)  # activation function
    except:
        print("Activation function invalid!")


class Node:
    def __init__(self, w):
        self.inpt = 0
        self.weights = w

    def add(self, val):
        self.inpt += val

    def reset(self):
        self.inpt = 0

    def get(self, i):
        return self.weights[i]

    def set(self, i, value):
        self.weights[i] = value

    def out(self, i):
        return self.weights[i] * self.inpt

    def activate(self):
        self.inpt = activation_function(self.inpt)


def initialize_cpso():
    global maxW, minW, bestError, CPSO, contextVec, structure
    for i in range(len(structure) - 1):
        layer = []
        for n in range(structure[i]):
            w = []
            for j in range(structure[i + 1]):
                w.append(random.uniform(minW, maxW))
            node = Node(w)
            layer.append(node)
        contextVec.append(layer)
    for i in range(len(structure) - 1):
        for j in range(structure[i]):  # for a given # of nodes in layer
            subSwarm = []
            for p in range(nSubParticles):  # for a given # of particles per swarm
                w = []
                for k in range(structure[i + 1]):  # # connections to prev layer
                    w.append(random.uniform(minW, maxW))
                particle = Particle(w)
                subSwarm.append(particle)
            CPSO.append(subSwarm)
    bestError = fitness()
    pass


def fitness():
    global structure, contextVec, show, inputData
    mse = 0
    random.shuffle(inputData)
    P = len(inputData)
    K = structure[len(structure) - 1]
    for inputs in inputData:
        _mse = 0  # mse for current data input
        din = []
        out = inputs[len(inputs) - 1]
        for i in range(len(inputs) - 1):
            din.append(inputs[i])
        # initial load
        for i in range(len(contextVec[0])):
            contextVec[0][i].reset()
            contextVec[0][i].add(din[i])
        output = feed_forward()
        if show:
            print(out, output)
        for i in range(len(out)):
            _mse += ((out[i] - output[i]) ** 2)
        _mse = _mse / K
        mse += _mse
        # print(din, out)
    mse = mse / P
    return mse


def feed_forward():
    global structure, contextVec
    output = []
    for i in range(1, len(contextVec)):
        for n in range(len(contextVec[i])):  # for every node in current layer
            contextVec[i][n].reset()
            for _n in range(len(contextVec[i - 1])):  # for every node in last layer
                contextVec[i][n].add(contextVec[i - 1][_n].out(n))
            contextVec[i][n].activate()
    for n in range(structure[len(structure) - 1]):
        o = 0
        for _n in range(len(contextVec[len(contextVec) - 1])):
            o += contextVec[len(contextVec) - 1][_n].out(n)
        o = activation_function(o)
        output.append(o)
    return output


def train(iterations):
    global bestError, W, c1, c2, show
    for i in range(iterations):
        # update context vector
        _n = 0
        for layer in range(len(contextVec)):
            for n in range(len(contextVec[layer])):
                for particle in CPSO[_n]:
                    for j in range(len(particle.weights)):
                        contextWeight = contextVec[layer][n].get(j)
                        contextVec[layer][n].set(j, particle.weights[j])
                        fit = fitness()
                        # print(bestError, particle.bValue, fit)
                        if fit < particle.bValue:
                            particle.bWeights[j] = particle.weights[j]
                            particle.bValue = fit
                        if fit > bestError:
                            contextVec[layer][n].set(j, contextWeight)
                        else:
                            bestError = fit
                        particle.velocities[j] = (W * particle.velocities[j]) + \
                                                 ((c1 * random.random()) * (
                                                         particle.bWeights[j] - particle.weights[j])) + \
                                                 ((c2 * random.random()) * (particle.bWeights[j] - particle.weights[j]))
                        if particle.velocities[j] > Vmax:
                            particle.velocities[j] = Vmax
                        if particle.velocities[j] < -Vmax:
                            particle.velocities[j] = -Vmax
                        particle.weights[j] += particle.velocities[j]
                _n += 1
        print(bestError)
        # # Break if iteration is found
        # if bestFitness < error:
        #     print("Solution found:", cVec, "error:", bestFitness, "application:",
        #           (cVec[0] * cVec[0]) + (cVec[1] * cVec[1]) + (cVec[2] * cVec[2]), "on iteration:", i)
        #     break
    # if bestFitness > error:
    #     print("Nearest possible solution:", cVec, "error:", bestFitness, "application:",
    #           (cVec[0] * cVec[0]) + (cVec[1] * cVec[1]) + (cVec[2] * cVec[2]))

    show = True
    fitness()
    show = False
    print("done")


def iris():
    global structure
    load_data("iris")
    structure = {
        0: 4,
        1: 4,
        2: 3
    }
    initialize_cpso()
    train(50)
    pass


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    global activationFunctions, tActivation
    activationFunctions = {
        "sigmoid": sigmoid,
        "tanh": tanh
    }
    config = {
        1: iris
    }

    config[1]()
