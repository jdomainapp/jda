export default class StructureConstructor {
    constructor(name, raw) {
        this.name = name
        this.raw = raw
        this.structure = this.buildTree(this.name, this.raw)
        this.iterator = 0
    }

    buildTree(prefixName, rawStructure) {
        var res = []
        for(var i = 0; i < rawStructure.length; i++) {
            res.push({
                "endpoint": (prefixName != "" ? prefixName + '-' : "") + rawStructure[i]["endpoint"],
                "name": rawStructure[i]["name"],
                "subItem": (rawStructure[i]["subItem"] && rawStructure[i]["subItem"].length > 0) ?
                            this.buildTree((prefixName != "" ? prefixName + '-' + rawStructure[i]["endpoint"]: ""), rawStructure[i]["subItem"]) :
                            undefined
            })
        }
        return res
    }

    resetItr() {
        this.iterator = 0
    }

    getCurrentProps() {
        if(this.structure.length > 0) {
            if(this.iterator >= this.structure.length) {
                this.resetItr()
            }
            return {id: this.structure[this.iterator]["endpoint"], structure: this.structure[this.iterator++]["subItem"]}
        } else {
            return {id: undefined, structure: undefined}
        }
    }

    skip(skipNum) {
        this.iterator += skipNum
    }

    getStructure() {
        return this.structure
    }
}