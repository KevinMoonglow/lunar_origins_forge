#!/usr/bin/env python

from typing import Any
import yaml, json, argparse, re

JsonValue = dict[str, 'JsonValue'] | list['JsonValue'] | str | int | float | bool

class OmitFile(Warning):
    pass

class KeepEmpty:
    heldObj: JsonValue
    def __init__(self, obj) -> None:
        self.heldObj = obj

def check_condition(content: dict[str, Any], flags: set[str]) -> bool:
    if content.get('flag'):
        f = content['flag']
        return bool((f in flags and not content.get('inverted')) or
               (f not in flags and content.get('inverted')))
    elif content.get('flags_any'):
        flag_list = content['flags_any']
        check = False
        for f in flag_list:
            if f in flags:
                check = True
                break

        return check
    elif content.get('flags_all'):
        flag_list = content['flags_all']
        check = True
        for f in flag_list:
            if f not in flags:
                check = False
                break

        return check

    return True



def preproc(cmd: str, content: JsonValue, flags: set[str], path: list[str]) -> JsonValue | None | KeepEmpty:
    if cmd == "if":
        if type(content) != dict:
            raise TypeError("$if expects an object")

        if check_condition(content, flags):
            return content.get("if_value")
        else:
            return content.get("else_value")

    elif cmd == "if_else_list":
        if type(content) != dict:
            raise TypeError(str.format("{}.${}, $if_else_list expects an object.", ".".join(path), cmd))

        values = content.get("values")
        assert(type(values) == list)

        for o in values:
            o: JsonValue
            if type(o) != dict:
                raise TypeError(str.format("{}.${}, $if_else_list.values should be a list of objects containing `value`.", ".".join(path), cmd))

            if check_condition(o, flags):
                return o.get('value')

        return None
    elif cmd == "" or cmd.startswith("-"):
        return None
    elif cmd == "empty":
        return KeepEmpty(content)

    elif cmd.startswith("$!"):
        f = cmd[2:]
        if f not in flags:
            return content
        else:
            return None

    elif cmd.startswith("$"):
        f = cmd[1:]
        if f in flags:
            return content
        else:
            return None

    else:
        raise ValueError(str.format("Unknown ${} directive.", cmd))



def process_object(o: JsonValue, flags: set[str], path: list[str], willTrimEmpty: bool = True) -> JsonValue | None:
    result: JsonValue | None

    if type(o) == dict:
        result = {}

        for k, v in o.items():
            m = re.match(r'^\$(.*)', k)
            if m:
                r = preproc(m[1], v, flags, path)
                if isinstance(r, KeepEmpty):
                    willTrimEmpty = False
                    


                    r = r.heldObj

                if r is not None:
                    p = process_object(r, flags, path + [k], False)
                    if type(p) == dict:
                        for kk, vv in p.items():
                            result[kk] = vv
                    elif len(o.keys()) == 1:
                        return p
                    else:
                        print(repr(o.keys()))
                        raise TypeError(str.format("{}.{}: Object Expected here, or too many preprocs.", ".".join(path), k))

            elif type(v) == dict or type(v) == list:
                r = process_object(v, flags, path + [k])
                if r != None:
                    result[k] = r
            else:
                result[k] = v
        if willTrimEmpty and len(result.keys()) == 0:
            return None

    elif type(o) == list:
        result = []
        for i, item in enumerate(o):
            p = process_object(item, flags, path + [str.format("[{}]", i)])
            if p != None:
                result.append(p)
        if willTrimEmpty and len(result) == 0:
            return None
    else:
        result = o

    return result








if __name__=='__main__':
    parser = argparse.ArgumentParser()

    parser.add_argument("input", type=argparse.FileType("r", encoding="utf-8"))
    parser.add_argument("-o", "--output", type=argparse.FileType("w", encoding="utf-8"))
    parser.add_argument("-f", "--flag", action='append', help="Preprocessor flags.")


    args = parser.parse_args()

    flags: set[str] = set(args.flag or [])

    with args.input as i:
        data = yaml.safe_load(i)

    data = process_object(data, flags, path=[])

    with args.output as o:
        json.dump(data, o, indent=2)
        print(file=o)
