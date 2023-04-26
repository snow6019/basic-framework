package com.lxzh.basic.framework.modular.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : qun.zheng
 * @Description: excel工具类
 * @date : 2020/6/2 16:25
 **/
@Slf4j
public class ExcelUtils {

    private static final String SEQ_HEAD = "序号";

    /**
     *  临时文件存储目录
     **/
    public static final String  HIS_TMP_DIR = "his" ;

    /**
     *  文件导入
     * @param file
     * @param dataListCallback
     * @return void
     **/
    public static <T> void importFile(File file,Class<T> entityClass, DataListCallback<T> dataListCallback){
        importFile(file, entityClass, 1, dataListCallback);
    }

    /**
     * 文件导入
     * @param file
     * @param entityClass
     * @param headRowNumber
     * @param dataListCallback
     * @param <T>
     */
    public static <T> void importFile(File file,Class<T> entityClass, Integer headRowNumber, DataListCallback<T> dataListCallback){
        EasyExcel.read(file, entityClass, new ImportDataListener(dataListCallback)).sheet()
                .headRowNumber(headRowNumber).doRead();
    }

    /**
     * 文件导入
     * @param file
     * @param entityClass
     * @param headRowNumber
     * @param dataListCallback
     * @param <T>
     */
    public static <T> void importFile(InputStream inputStream, Class<T> entityClass, Integer headRowNumber, DataListCallback<T> dataListCallback){
        EasyExcel.read(inputStream, entityClass, new ImportDataListener(dataListCallback)).sheet()
                .headRowNumber(headRowNumber).doRead();
    }

    /**
     *  对于数据量较大的场景可分页查询 导入
     * @param outputStream
     * @param head 标注 {@link ExcelProperty} 注解的Class
     * @param dataScroll
     * @param enableSeq
     * @return void
     **/
    public static <T> void exportWithPageable(OutputStream outputStream, Class<T> head, DataScroll<T> dataScroll, boolean enableSeq) {
        // 这里 需要指定写用哪个class去写
        ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(outputStream, head)
                .autoCloseStream(Boolean.FALSE);
        if (enableSeq) {
            excelWriterBuilder.registerWriteHandler(new SeqCellWriteHandler());
        }

        ExcelWriter excelWriter = excelWriterBuilder.build();
        // 这里注意 如果同一个sheet只要创建一次
        WriteSheet writeSheet = EasyExcel.writerSheet(0,null).build();

        scrollData(dataScroll, dataList -> {
            excelWriter.write(dataList, writeSheet);
        });
        /// 千万别忘记finish 会帮忙关闭流
        excelWriter.finish();
    }

    private static class MatchColumnWidthStyleStrategy extends AbstractColumnWidthStyleStrategy {
        private static final int MAX_COLUMN_WIDTH = 255;

        private static final Map<Integer, Map<Integer, Integer>> CACHE = new HashMap<Integer, Map<Integer, Integer>>(8);

        @Override
        protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<CellData> cellDataList, Cell cell, Head head,
                                      Integer relativeRowIndex, Boolean isHead) {
            boolean needSetWidth = isHead || !com.alibaba.excel.util.CollectionUtils.isEmpty(cellDataList);
            if (!needSetWidth) {
                return;
            }
            Map<Integer, Integer> maxColumnWidthMap = CACHE.get(writeSheetHolder.getSheetNo());
            if (maxColumnWidthMap == null) {
                maxColumnWidthMap = new HashMap<Integer, Integer>(16);
                CACHE.put(writeSheetHolder.getSheetNo(), maxColumnWidthMap);
            }
            Integer columnWidth = dataLength(cellDataList, cell, isHead);
            if (columnWidth < 0) {
                return;
            }
            if (columnWidth > MAX_COLUMN_WIDTH) {
                columnWidth = MAX_COLUMN_WIDTH;
            }
            Integer maxColumnWidth = maxColumnWidthMap.get(cell.getColumnIndex());
            if (maxColumnWidth == null || columnWidth > maxColumnWidth) {
                maxColumnWidthMap.put(cell.getColumnIndex(), columnWidth);
                writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), columnWidth * 256);
            }
        }

        private Integer dataLength(List<CellData> cellDataList, Cell cell, Boolean isHead) {
            if (isHead) {
                return cell.getStringCellValue().getBytes().length * 2;
            }
            CellData cellData = cellDataList.get(0);
            CellDataTypeEnum type = cellData.getType();
            if (type == null) {
                return -1;
            }
            switch (type) {
                case STRING:
                    return cellData.getStringValue().getBytes().length;
                case BOOLEAN:
                    return cellData.getBooleanValue().toString().getBytes().length;
                case NUMBER:
                    return cellData.getNumberValue().toString().getBytes().length;
                default:
                    return -1;
            }
        }
    }




    /**
     * 导入数据接口实现类
     * @Author wr
     * @Date 2020/6/11 14:05
     **/
    public static class ImportDataListener<T> extends AnalysisEventListener<T> {
        private static final Logger LOGGER = LoggerFactory.getLogger(ImportDataListener.class);
        /**
         * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
         */
        private static final int BATCH_COUNT = 3000 ;
        private int count = 0 ;
        private List<T> list = new ArrayList();
        private DataListCallback<T> dataListCallback;

        public ImportDataListener() {
            super();
        }

        public ImportDataListener(DataListCallback<T> dataListCallback) {
            super();
            this.dataListCallback = dataListCallback;
        }

        /**
         * 这个每一条数据解析都会来调用
         *
         * @param data            one row value. Is is same as {@link AnalysisContext#readRowHolder()}
         * @param analysisContext
         */
        @Override
        public void invoke(T data, AnalysisContext analysisContext) {
            LOGGER.info("解析到一条数据:{}", JSON.toJSONString(data));
            list.add(data);
            // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
            if (list.size() >= BATCH_COUNT) {
                count = count + list.size();
                saveData();
                // 存储完成清理 list
                list.clear();
            }
        }

        /**
         * 所有数据解析完成了 都会来调用
         *
         * @param context
         */
        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // 这里也要保存数据，确保最后遗留的数据也存储到数据库
            count = count + list.size();
            saveData();
            LOGGER.info("所有数据解析完成！");
        }

        /**
         * 加上存储数据库
         */
        public void saveData() {
            LOGGER.info("{}条数据，开始存储数据库！", list.size());
            dataListCallback.doWith(list);
            LOGGER.info("存储数据库成功！");
        }
    }

    /**
     *  分页数据查询
     * @Author qun.zheng
     * @Date 2020/6/3 11:28
     **/
    public static interface DataScroll<T>{
        /**
         * 获取总条数
         **/
        long getTotal();

        /**
         *  根据分页信息 进行 数据的相关处理
         * @param page base 1
         * @param pageSize
         **/
        List<T> getDataList(int page, int pageSize);

        default int getPageSize(){
            return 1000;
        }
    }

    /**
     *  数据列表处理
     * @Author qun.zheng
     * @Date 2020/6/3 11:50
     **/
    public static interface DataListCallback<T>{

        void doWith(List<T> dataList);
    }


    /**
     *  批量查询数据
     * @param dataScroll
     * @return void
     **/
    public static <T> void scrollData(DataScroll<T> dataScroll,DataListCallback<T> dataListCallback){
        int curPage = 1;
        int pageSize = dataScroll.getPageSize();

        int totalPage = getTotalPage(dataScroll.getTotal(),pageSize);

        while (true) {
            if(curPage > totalPage){
                break;
            }

            List<T> dataList = dataScroll.getDataList(curPage, pageSize);
            //额外添加序号
            dataListCallback.doWith(dataList);

            ++curPage;
        }

    }

    public static int getTotalPage(long total, int pageSize) {
        int totalPage = (int) (total / pageSize);
        if (total % pageSize != 0) {
            totalPage += 1;
        }

        return totalPage;
    }

    private static String getPath() {
        return ExcelUtils.class.getResource("/").getPath();
    }

    private static class SeqCellWriteHandler implements CellWriteHandler{

        private int curIndex = 0;

        @Override
        public void beforeCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Head head, Integer columnIndex, Integer relativeRowIndex, Boolean isHead) {

        }

        @Override
        public void afterCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {

        }

        @Override
        public void afterCellDataConverted(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, CellData cellData, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {

        }

        @Override
        public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, List<CellData> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
            List<String> headNameList = head.getHeadNameList();
            if(CollectionUtils.isEmpty(headNameList)){
               return;
            }

            String headName = headNameList.get(0);
            if (SEQ_HEAD.equals(headName) && !isHead) {
                cell.setCellValue(String.valueOf(++curIndex));
            }

        }
    }


    public static void main(String[] args) throws Exception {
        File dest = new File("");
        ExcelUtils.importFile(dest, DisasterHistoryMonthStatisticsVoImport.class, (dataList) -> {
            System.out.println("dataList = " + dataList);
        });
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @ApiModel(value = "月度灾变统计")
    public class DisasterHistoryMonthStatisticsVoImport {

        @ApiModelProperty(value = "数据来源")
        @ExcelProperty(index = 0)
        private String source;

        @ApiModelProperty(value = "时间尺度")
        @ExcelProperty(index = 1)
        private String timeScale;

        @ApiModelProperty(value = "年")
        @ExcelProperty(index = 2)
        private String year;

        @ApiModelProperty(value = "月")
        @ExcelProperty(index = 3)
        private String month;
    }
}
