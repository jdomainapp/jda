export default class StructureConstructor {
    constructor(props) {
        this.name = props.name
        this.raw = props.raw
        this.structure = this.buildTree(this.name, this.raw)
    }

    buildTree(prefixName, rawStructure) {
        var res = rawStructure
        for(var i = 0; i < res.length; i++) {
            // console.log(prefixName + "-" + res[i]["endpoint"])   
            // console.log(prefixName + "-" + res[i]["endpoint"])
            res[i]["endpoint"] = `${prefixName}-${res[i]["endpoint"]}`
            if(res[i]["subItem"] && res[i]["subItem"].length > 0) {
                res[i]["subItem"] = this.buildTree(res[i]["endpoint"], res[i]["subItem"])
            }
        }
        return res
    }

    getStructure() {
        return this.structure
    }
}