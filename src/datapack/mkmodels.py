#!/usr/bin/env python

from pathlib import Path
import yaml
import json
import argparse
import os.path


JsonData = dict[str, 'JsonData'] | list['JsonData'] | str | int | float
JsonPath = list[str | int]

def printJsonPath(path: JsonPath):
    textParts: list[str] = []
    for i, v in enumerate(path):
        if type(v) == int:
            textParts.append(str.format("[{}]", v))
        elif type(v) == str:
            if i > 0:
                textParts.append('.')
            textParts.append(v)

    return "".join(textParts)

def buildModel(name: str, data: JsonData, targetPath: Path, jsonPath: JsonPath):
    if type(data) == str:
        model: JsonData = {
            'parent': 'item/generated',
            'textures': {
                'layer0': data,
            }
        }
        return model

    elif type(data) == dict:
        data = data.copy()
        if 'parent' not in data:
            data['parent'] = 'item/generated'

        return data

    else:
        raise ValueError(str.format("{} expected to be a string or object.",
                                    printJsonPath(jsonPath + [name])))









def writeModel(name: str, data: JsonData, targetPath: Path):
    modelPath = targetPath / (name + ".json")

    with modelPath.open('w', encoding='utf-8') as fp:
        json.dump(data, fp=fp, indent=2)
        fp.write('\n')
        

    




def processEntry(data: JsonData, rootPath: Path, jsonPath: JsonPath):
    assert type(data) == dict, str.format("YAML Entry at {} should be an object.",
                                          printJsonPath(jsonPath))

    pathDef = data['$target']
    assert type(pathDef) == str

    targetPath = rootPath / pathDef
    targetPath.mkdir(parents=True, exist_ok=True)

    for k, v in data.items():
        if not k.startswith('$'):
            model = buildModel(k, v, targetPath, jsonPath)
            writeModel(k, model, targetPath)



def processRoot(data: JsonData, rootPath: Path, jsonPath: JsonPath):

    assert type(data) == dict, 'YAML Root should be an object.'
    for k, v in data.items():
        assert type(v) == dict, "Entry should be an object."
        processEntry(v, rootPath, jsonPath + [k])
    


def mkpath(pathstr: str):
    path = Path(pathstr).resolve()
    #if not str(path).startswith(os.getcwd()):
    #    raise ValueError("Not within project directory.")

    return path



if __name__=='__main__':
    parser = argparse.ArgumentParser()

    parser.add_argument('input', type=argparse.FileType("r", encoding="utf-8"))
    parser.add_argument('-o', '--output', type=str, required=True,
                        help="Root path to output all files to.")

    args = parser.parse_args()

    with args.input as i:
        data = yaml.safe_load(i)

    rootPath = mkpath(args.output)

    processRoot(data, rootPath, [])
