package view.Helper;
//lớp này dùng để lưu ảnh
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class SaveImage {

    private static final String IMAGE_DIR = "storage/images"; // lưu vào storage/images nếu images chưa có thì tự tạo mới

    /**
     * Copy ảnh vào storage/images và trả về path để lưu DB
     * @param sourceFile file gốc user chọn
     * @return path tương đối (vd: images/abc.png) hoặc null nếu lỗi
     */
    public static String copyToStorage(File sourceFile) {
        if (sourceFile == null || !sourceFile.exists()) return null;

        try {
            // Tạo thư mục nếu chưa tồn tại
            File dir = new File(IMAGE_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Lấy extension
            String ext = getFileExtension(sourceFile.getName());

            // Tạo tên file unique
            String fileName = UUID.randomUUID() + ext;

            Path targetPath = Path.of(IMAGE_DIR, fileName);

            Files.copy(
                    sourceFile.toPath(),
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            // Trả về path lưu DB (KHÔNG có "storage/")
            return "images/" + fileName;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getFileExtension(String name) {
        int dot = name.lastIndexOf('.');
        return dot >= 0 ? name.substring(dot) : "";
    }
}