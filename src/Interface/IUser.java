package Interface;

/* Đại diện cho người dùng trong hệ thống (nhân viên, quản lý) */
public interface IUser {
    String getId();         // id user
    String getUsername();   // tên đăng nhập
    String getRole();       // phân quyền: "admin" / "staff"
}
