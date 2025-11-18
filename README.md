# Coffee Shop Management System (Java OOP)

## Giới thiệu Dự án
Dự án xây dựng ứng dụng quản lý quán cà phê sử dụng ngôn ngữ Java. Hệ thống được thiết kế tuân thủ chặt chẽ 4 nguyên tắc cơ bản của Lập trình hướng đối tượng (OOP):
- **Encapsulation (Đóng gói)**: Bảo vệ dữ liệu thông qua các access modifiers.
- **Inheritance (Kế thừa)**: Tái sử dụng mã nguồn giữa các loại món (Drink, Food).
- **Polymorphism (Đa hình)**: Xử lý linh hoạt các phương thức hiển thị và tính giá.
- **Abstraction (Trừu tượng)**: Sử dụng Interface và Abstract Class để định nghĩa khung sườn.

## Thành viên & Phân công
* **Front-End Team**: Xử lý giao diện người dùng (Java Swing/JavaFX).
* **Back-End Team**: Xử lý logic nghiệp vụ, dữ liệu và cấu trúc OOP.

---

# Hướng dẫn Workflow với GitHub

Tài liệu này mô tả quy trình làm việc chuẩn khi đóng góp mã nguồn vào repository của dự án. Việc tuân thủ quy trình này đảm bảo mã nguồn luôn ổn định và giảm thiểu xung đột (Conflict).

## Quy tắc Vàng

**KHÔNG BAO GIỜ push (đẩy) code trực tiếp lên nhánh `main`.**

Nhánh `main` phải luôn phản ánh phiên bản ổn định, sẵn sàng chạy của ứng dụng. Mọi thay đổi đều phải được thực hiện thông qua quy trình Pull Request (PR) và được review bởi thành viên khác.

## Quy trình Làm việc Chuẩn (Feature Branch Workflow)

Quy trình này áp dụng cho việc thêm tính năng mới (như thêm món, tạo giao diện), sửa lỗi logic, hoặc cập nhật tài liệu.

### Bước 1: Bắt đầu một Tác vụ Mới

Trước khi bắt đầu code cho một tác vụ cụ thể, hãy thực hiện các bước sau:

1.  **Cập nhật Nhánh `main`:** Đảm bảo bạn có phiên bản mới nhất của mã nguồn từ kho lưu trữ chung.
    ```bash
    git checkout main
    git pull origin main
    ```

2.  **Tạo Nhánh Mới (Feature Branch):** Tạo một nhánh riêng cho tác vụ của bạn. Đặt tên nhánh rõ ràng, thể hiện mục đích của nó.
    * **Quy ước đặt tên:** `feature/feature-name` (tính năng mới) hoặc `bugfix/bug-description` (sửa lỗi).
    * **Ví dụ cho BE:** `feature/create-menu-service`, `feature/add-tea-menu`
    * **Ví dụ cho FE:** `feature/design-main-ui`, `bugfix/fix-search-bar`
    
    ```bash
    git checkout -b feature/create-menu-service
    ```
    Lệnh này tạo nhánh mới và tự động chuyển bạn sang làm việc trên nhánh đó.

### Bước 2: Làm việc và Lưu (Commit)

1.  **Viết mã:** Thực hiện các thay đổi, thêm tệp mới (ví dụ: `Coffee.java`, `MainFrame.java`), sửa lỗi trên nhánh mới của bạn.
2.  **Lưu Thay đổi (Commit):** Lưu công việc của bạn thường xuyên bằng các "commit". Mỗi commit nên đại diện cho một thay đổi nhỏ, hoàn chỉnh và có ý nghĩa.
    * Xem các tệp đã thay đổi: `git status`
    * Thêm các tệp bạn muốn lưu vào "staging area": `git add .`
    * Tạo commit với một thông điệp rõ ràng:
        ```bash
        git commit -m "Add: Implement IDiscountable interface for Pastry class"
        # Or:
        git commit -m "Fix: Total price calculation error in Order View"
        ```
    * **Lưu ý:** Viết commit message bằng tiếng Anh. Thông điệp nên bắt đầu bằng động từ (Add, Fix, Update, Remove,...).

### Bước 3: Đẩy (Push) Nhánh Lên GitHub

Khi bạn đã hoàn thành một phần công việc đáng kể hoặc toàn bộ tính năng/sửa lỗi, hãy đẩy nhánh của bạn lên kho lưu trữ GitHub chung:

```bash
git push origin feature/create-menu-service
```
# Phân Chia Công Việc & Tiến Độ (Task Distribution)

Dự án được chia thành 2 nhóm chính: **Front-End (UI)** và **Back-End (Core Logic)**. Mọi giao tiếp giữa hai bên đều thông qua Interface `IMenuService`.

## Nhóm Front-End (Giao diện người dùng)
**Phạm vi:** Package `view`

### FE 1: Main Layout & Data Display
Chịu trách nhiệm về cửa sổ chính và hiển thị dữ liệu.
- [ ] **Thiết kế MainFrame**: Tạo cửa sổ chính, thanh tiêu đề, bố cục (BorderLayout/GridBagLayout).
- [ ] **Menu Table**: Tạo `JTable` (hoặc ListView) để hiển thị danh sách món ăn/đồ uống.
- [ ] **Data Binding**: Viết hàm nhận `List<MenuItem>` từ Service và đổ dữ liệu vào bảng.
- [ ] **Search UI**: Tạo thanh tìm kiếm và nút Filter trên giao diện chính.

### FE 2: User Interaction & Dialogs
Chịu trách nhiệm về các hành động thêm/sửa/xóa và cửa sổ nhập liệu.
- [ ] **Add/Edit Dialogs**: Thiết kế Form nhập liệu (JDialog) cho Coffee, Tea, Pastry (xử lý ẩn hiện các trường nhập liệu tùy theo loại món).
- [ ] **Event Handling**: Bắt sự kiện click nút Thêm, Xóa, Sửa.
- [ ] **Validation**: Kiểm tra dữ liệu đầu vào (giá phải là số, tên không để trống) trước khi gọi xuống BE.
- [ ] **Integration**: Gọi các hàm `addItem`, `removeItem` thông qua `IMenuService`.

---

## Nhóm Back-End (Xử lý nghiệp vụ & Dữ liệu)
**Phạm vi:** Package `model`, `service`, `storage`

### BE 1: OOP Architect (Models)
Chịu trách nhiệm xây dựng nền tảng đối tượng.
- [ ] **Abstract Classes**: Cài đặt `MenuItem`, `Drink`, `Food` với các phương thức trừu tượng.
- [ ] **Concrete Classes**: Cài đặt `Coffee`, `Tea`, `Pastry`.
- [ ] **Polymorphism**: Override phương thức `displayDetails()` cho từng lớp con để hiển thị thông tin riêng biệt (Size, Sugar, Ice...).
- [ ] **Interface Implementation**: Cài đặt `IDiscountable` cho các lớp được giảm giá.

### BE 2: Service Layer (Core Logic)
Chịu trách nhiệm về luồng đi của dữ liệu (Trái tim của ứng dụng).
- [ ] **Interfaces**: Định nghĩa `IMenuService` (Hợp đồng với FE) và `IDataStorage` (Hợp đồng với Storage).
- [ ] **Menu Class**: Quản lý `List<MenuItem>` trong bộ nhớ (Memory).
- [ ] **MenuService**: Cài đặt logic nghiệp vụ:
    - [ ] Thêm món (Check trùng ID).
    - [ ] Xóa món (Check tồn tại).
    - [ ] Tìm kiếm/Lọc món ăn.
    - [ ] Tính toán giảm giá.

### BE 3: Data Persistence (Storage)
Chịu trách nhiệm lưu trữ dữ liệu xuống file.
- [ ] **FileTextStorage**: Cài đặt `IDataStorage`.
- [ ] **Save Logic**: Duyệt danh sách object -> Chuyển thành chuỗi CSV (Ví dụ: `COFFEE,001,Latte,30000...`) -> Ghi xuống file `data.txt`.
- [ ] **Load Logic**: Đọc file `data.txt` -> Parse chuỗi -> Dùng `MenuItemFactory` để tạo lại object -> Trả về Menu.
- [ ] **Utils**: Hỗ trợ viết class `MenuItemFactory` để tạo object từ chuỗi String đọc được.

---

## Integration (Tích hợp)
Phần việc chung của cả nhóm.
- [ ] **Main Entry**: Viết class `Main.java` để khởi tạo `Storage` -> `Service` -> `View`.
- [ ] **Final Testing**: Kiểm tra toàn bộ luồng từ Giao diện -> Logic -> File và ngược lại.