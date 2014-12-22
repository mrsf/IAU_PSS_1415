package pt.ipleiria.estg.meicm.iaupss.estgparking.database;

import android.provider.BaseColumns;

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
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String IMAGE_PATH = "image_path";
    }

    public static abstract class SectionBase implements BaseColumns {
        public static final String TABLE_NAME = "section";
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String CAPACITY = "capacity";
        public static final String OCCUPATION = "occupation";
        public static final String LOT_ID = "lot_id";
    }

    public static abstract class RankingBase implements BaseColumns {
        public static final String TABLE_NAME = "ranking";
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String POINTS = "points";
        public static final String IMAGE_PATH = "image_path";
    }
}
