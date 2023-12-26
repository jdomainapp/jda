export default class StructureConstructor {
    constructor(props) {
        this.name = props.name
        this.raw = props.raw
        this.structure = this.buildTree(this.name, this.raw)
    }

    buildTree(prefixName, rawStructure) {
        var res = []
        for(var i = 0; i < rawStructure.length; i++) {
            res.push({
                "endpoint": prefixName + '-' + rawStructure[i]["endpoint"],
                "name": rawStructure[i]["name"],
                "subItem": (rawStructure[i]["subItem"] && rawStructure[i]["subItem"].length > 0) ?
                            this.buildTree(prefixName + '-' + rawStructure[i]["endpoint"], rawStructure[i]["subItem"]) :
                            undefined
            })
        }
        return res
    }

    getStructure() {
        return this.structure
    }
}