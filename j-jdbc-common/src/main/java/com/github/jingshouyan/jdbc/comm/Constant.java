package com.github.jingshouyan.jdbc.comm;

import java.util.Optional;

/**
 * @author jingshouyan
 * 11/22/18 4:52 PM
 */
public interface Constant {
    long NO_DELETE = -1;
    int ID_FIELD_LENGTH = 50;
    int COLUMN_ORDER_DEFAULT = 10;
    int COLUMN_LENGTH_DEFAULT = 255;
    int VARCHAR_MAX_LENGTH = 5000;

    int PAGE_DEFAULT = 1;
    int PAGE_SIZE_DEFAULT = 10;

    String COLUMN_ENCRYPT_KEY_PREFIX_FIXED = "fixed:";
    String COLUMN_ENCRYPT_KEY_PREFIX_FIELD = "field:";
    String COLUMN_ENCRYPT_KEY_DEFAULT = COLUMN_ENCRYPT_KEY_PREFIX_FIXED +"abcd1234!@#$";


}
