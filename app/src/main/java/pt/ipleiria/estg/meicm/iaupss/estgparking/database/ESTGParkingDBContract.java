package pt.ipleiria.estg.meicm.iaupss.estgparking.database;

import android.provider.BaseColumns;

/**
 * Created by francisco on 17-12-2014.
 */
abstract class ESTGParkingDBContract {

    public static final String PRIMARY_KEY = " PRIMARY KEY";
    public static final String TEXT_TYPE = " TEXT";
    public static final String INTEGER_TYPE = " INTEGER";
    public static final String REAL_TYPE = " REAL";
    public static final String NUMERIC_TYPE = " NUMERIC";
    public static final String COMMA_SEP = ", ";

    private ESTGParkingDBContract() {
    }

    public static abstract class LotBase implements BaseColumns {
        public static final String TABLE_NAME = "lot";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String IMAGE_PATH = "image_path";
    }

    public static abstract class SectionBase implements BaseColumns {
        public static final String TABLE_NAME = "section";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String CAPACITY = "lotacao";
        public static final String OCCUPATION = "ocupacao";
        public static final String LOT_ID = "id_lote";
    }

}
