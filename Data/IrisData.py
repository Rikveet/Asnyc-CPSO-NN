import torch
from torch.utils.data import Dataset
from torch.utils.data.dataset import T_co


class IrisDataSet(Dataset):
    def __init__(self):
        file = open("Data/RawData/iris.txt", "r").read().replace("Iris-setosa", "1").replace("Iris-versicolor",
                                                                                             "2").replace(
            "Iris-virginica", "3").strip().split("\n")
        self.data = []
        for i in range(len(file)):
            f = file[i].split(",")
            if len(f) > 0:
                self.data.append({
                    "input": torch.FloatTensor([float(val) for val in f[0:len(f) - 1]]),
                    "expected": float(f[len(f) - 1])
                })

    def __len__(self):
        return len(self.data)

    def __getitem__(self, index) -> T_co:
        return self.data[index]["input"], self.data[index]["expected"]
