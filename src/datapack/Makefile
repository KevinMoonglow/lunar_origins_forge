YAML_FILES:=$(shell fd -eyaml . data)
JSON_FILES:=$(YAML_FILES:data/%.yaml=../main/resources/data/%.json)


LUNAR_ORIGINS_DIRS:=lunar_origins/recipes minecraft/tags c/tags/items lunar_origins/powers lunar_origins/tags/items trinkets/entities trinkets/tags/items/head lunar_origins/tags/worldgen/biome

REALDIRS:=$(addprefix ../main/resources/data/,$(LUNAR_ORIGINS_DIRS))

default: FLAGS:=-f smithing_template
default: $(JSON_FILES) models

$(JSON_FILES): $(REALDIRS)

../main/resources/data/%.json: data/%.yaml
	./yaml2json.py $< -o $@ $(FLAGS)

$(REALDIRS):
	mkdir -p $@

.PHONY: clean models

clean:
	-rm -r ../main/resources/data


models: model_defs.yaml
	./mkmodels.py model_defs.yaml -o ../main/resources/assets

