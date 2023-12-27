export const @slot{{moduleName}}Structure=[
    @loop{endpoints}[[
    {
        "endpoint":"@slot{{field_name}}",
        "name":"@slot{{fieldLabel}}",
        "subItem": @slot{{subStructure}}
    },
    ]]loop{endpoints}@
]