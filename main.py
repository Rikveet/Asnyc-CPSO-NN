import torch
from torch.utils.data import DataLoader
import torch.nn as nn
from Data import IrisData

config = {
    "iris": {
        "data": IrisData.IrisDataSet(),
        "batchSize": 1,
        "dimensions": [4, 4, 3],
        "model": None,
        "C1": 0.8,
        "C2": 0.8,
        "Vmax": 0.5,
        "iter": 100,
        "particles": 20,
    }
}


class Model(nn.Module):
    def __init__(self, modelconfig):
        super(Model, self).__init__()
        self.layers = []
        for i in range(len(modelconfig) - 1):
            self.layers.append(nn.Linear(modelconfig[i], modelconfig[i + 1], bias=False))

    def forward(self, x):
        for layer in range(len(self.layers)):
            x = torch.tanh(self.layers[layer](x))
        return x


class CPSO:

    def __init__(self, dataType):
        self.select = dataType

    def train(self):
        dataset = config["iris"]["data"]
        data_loader = DataLoader(dataset=dataset, batch_size=config["iris"]["batchSize"], shuffle=True)
        config[self.select]["model"] = Model(config[self.select]["dimensions"])
        network = config[self.select]["model"]
        with torch.no_grad():
            for i, o in data_loader:
                out = network(i)
                print(out.tolist()[0])


if __name__ == "__main__":
    optimizer = CPSO("iris")
    optimizer.train()
