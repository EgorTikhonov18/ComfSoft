package ru.comfsoft.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.PriorityQueue;

@RestController
@RequestMapping("/api")
@Tag(name = "XLSX Reader API", description = "Поиск N максимального числа из файла")
public class XlsxController {
    @GetMapping("/nth-max")
    public int findNthMax(@RequestParam String filePath, @RequestParam int n) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("Файл не найден: " + filePath);
        }

        PriorityQueue<Integer> minHeap = new PriorityQueue<>(n);
        int count = 0;

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                for (Cell cell : row) {
                    if (cell.getCellType() == CellType.NUMERIC) {
                        int num = (int) cell.getNumericCellValue();
                        count++;
                        if (minHeap.size() < n) {
                            minHeap.add(num);
                        } else if (num > minHeap.peek()) {
                            minHeap.poll();
                            minHeap.add(num);
                        }
                    }
                }
            }
        }

        if (count < n) {
            throw new IllegalArgumentException("В файле только " + count + " чисел, но запрошено N=" + n);
        }

        return minHeap.peek();
    }
}

