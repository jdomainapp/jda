export const @slot{{moduleName}}Struct=[
    @loop{endpoints}[[
    {
        "endpoint":"@slot{{field_name}}",
        "name":"@slot{{fieldLabel}}",
        "subItem": @slot{{subStructure}}
    },
    ]]loop{endpoints}@
]