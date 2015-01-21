package pt.ipleiria.estg.meicm.iaupss.estgparking.database;

import android.provider.BaseColumns;

abstract class ESTGParkingDBContract {

    public static final String PRIMARY_KEY = " PRIMARY KEY";
    public static final String TEXT_TYPE = " TEXT";
    public static final String INTEGER_TYPE = " INTEGER";
    public static final String NUMERIC_TYPE = " NUMERIC";
    public static final String COMMA_SEP = ", ";

    private ESTGParkingDBContract() {
    }

    public static abstract class LotBase implements BaseColumns {
        public static final String TABLE_NAME = "lot";
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String LATITUDE_A = "latitude_a";
        public static final String LONGITUDE_A = "longitude_a";
        public static final String LATITUDE_B = "latitude_b";
        public static final String LONGITUDE_B = "longitude_b";
        public static final String LATITUDE_C = "latitude_c";
        public static final String LONGITUDE_C = "longitude_c";
        public static final String LATITUDE_D = "latitude_d";
        public static final String LONGITUDE_D = "longitude_d";
        public static final String IMAGE_PATH = "image_path";
    }

    public static abstract class SectionBase implements BaseColumns {
        public static final String TABLE_NAME = "section";
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String LATITUDE_A = "latitude_a";
        public static final String LONGITUDE_A = "longitude_a";
        public static final String LATITUDE_B = "latitude_b";
        public static final String LONGITUDE_B = "longitude_b";
        public static final String LATITUDE_C = "latitude_c";
        public static final String LONGITUDE_C = "longitude_c";
        public static final String LATITUDE_D = "latitude_d";
        public static final String LONGITUDE_D = "longitude_d";
        public static final String CAPACITY = "capacity";
        public static final String OCCUPATION = "occupation";
        public static final String LOT_ID = "lot_id";
    }

    public static abstract class RankingBase implements BaseColumns {
        public static final String TABLE_NAME = "ranking";
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String EMAIL = "email";
        public static final String SCORE = "score";
        public static final String IMAGE_PATH = "image_path";
    }

    public static abstract class LocationBase implements BaseColumns {
        public static final String TABLE_NAME = "location";
        public static final String ID = "id";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String TIME = "time";
        public static final String EMAIL = "email";
        public static final String SECTION_ID = "section_id";
    }

    public static abstract class StatisticBase implements BaseColumns {
        public static final String TABLE_NAME = "statistic";
        public static final String ID = "id";
        public static final String WEEK_DAY = "weekday";
        public static final String HOUR = "hour";
        public static final String OCCUPATION = "occupation";
        public static final String SECTION_ID = "section_id";
    }
}
