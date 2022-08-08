export default class Message {  
@loop{ModuleMessages}[[
    //@slot{{module__name}}
    static ADD_@slot{{MODULE_NAME}}_SUC = "Thêm mới @slot{{module__name}} thành công!";
    static ADD_@slot{{MODULE_NAME}}_ERR = "Thêm mới @slot{{module__name}} thất bại!";
    static UPDATE_@slot{{MODULE_NAME}}_SUC = "Cập nhật @slot{{module__name}} thành công!";
    static UPDATE_@slot{{MODULE_NAME}}_ERR = "Cập nhật @slot{{module__name}} thất bại!";
    static GET_@slot{{MODULE_NAME}}_ERR = "Lấy @slot{{module__name}} thất bại!";
    static GET_LIST_@slot{{MODULE_NAME}}_ERR = "Lấy danh sách @slot{{module__name}} thất bại!";
    static DELETE_@slot{{MODULE_NAME}}_SUC = "Xóa @slot{{module__name}} thành công!";
    static DELETE_@slot{{MODULE_NAME}}_ERR = "Xóa @slot{{module__name}} thất bại!";
]]loop{ModuleMessages}@
}