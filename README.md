# Java PDF Quiz Parser

Một công cụ nhỏ bằng Java hỗ trợ tự động trích xuất nội dung câu hỏi trắc nghiệm từ file PDF (nhận diện các đáp án đã được highlight) để phục vụ cho việc nhập liệu vào tool trộn đề.

## Tính năng chính
* Đọc và phân tích cấu trúc file PDF chứa đề trắc nghiệm.
* Tự động nhận diện các đoạn text được **Highlight (đáp án)** dựa trên Annotation tọa độ.
* Xuất dữ liệu cấu trúc câu hỏi sang định dạng text chuẩn hoặc JSON (sử dụng Gson) để dễ dàng tích hợp với tool trộn câu hỏi.

## Công nghệ sử dụng
* **Ngôn ngữ:** Java (Môi trường phát triển: Java 24)
* **Quản lý dự án:** Maven
* **Thư viện chính:**
    * [Apache PDFBox (v2.0.29)](https://github.com/apache/pdfbox) - Xử lý cấu trúc và trích xuất dữ liệu PDF.
    * [Google Gson (v2.10.1)](https://github.com/google/gson) - Cấu trúc hóa dữ liệu đầu ra thành JSON.