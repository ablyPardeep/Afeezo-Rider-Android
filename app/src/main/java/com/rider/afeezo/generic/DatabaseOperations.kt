package com.rider.afeezo.generic

import android.database.sqlite.SQLiteOpenHelper
import com.rider.afeezo.generic.DatabaseOperations
import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues
import android.content.Context
import java.lang.Exception
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class DatabaseOperations
    (var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    var objDatabase: SQLiteDatabase? = null
    override fun onCreate(objSqlLiteDatabase: SQLiteDatabase) {
        try {
            objSqlLiteDatabase.execSQL(CREATE_RECENT_SEARCH_TABLE)
        } catch (errorException: Exception) {

        }
    }

    override fun onUpgrade(objDb: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        println("Database upgrade in progress , old version : $oldVersion new version $newVersion")
        /*     switch (oldVersion) {
            //Rating
            case 1:
                String alterAppointmentSettingTable = "ALTER TABLE " + APPOINTMENT_SETTING_TABLE + " ADD COLUMN " + COLUMN_MULTIPLE_APPOINTMENTS + " TEXT ";
                objDb.execSQL(alterAppointmentSettingTable);
                System.out.println("Alter table APPOINTMENT_SETTING_TABLE");
                String alterUserRecordTable = "ALTER TABLE " + USER_RECORD_TABLE + " ADD COLUMN " + COLUMN_USER_RECORD_DOB + " TEXT ";
                objDb.execSQL(alterUserRecordTable);
                System.out.println("Alter table USER_RECORD_TABLE");
                String alterUserAppointmentTable = "ALTER TABLE " + USER_APPOINTMENTS_TABLE + " ADD COLUMN " + COLUMN_APPOINTMENT_RESCHEDULE + " TEXT ";
                objDb.execSQL(alterUserAppointmentTable);
                System.out.println("Alter table USER_APPOINTMENTS_TABLE");
            case 2:
                updateMyClinicDatabase(objDb);
            case 3:
                Log.e("Case 3 onUpgrade called", "");
                objDb.execSQL(CREATE_ASK_QUESTIONS);
                System.out.println("Successfully created ask question table");
            case 4:
                Log.e("Case 3 onUpgrade called", "");
                objDb.execSQL(SMS_SETTINGS);
                insertSmsSettingDetails(context);
                System.out.println("Successfully created sms setings table");
//
               */
        /* //creation of my clinic tables. -- start
               updateMyClinicDatabase(objDb);
                //creation of my clinic tables. -- end*/
        /*


            default:
                break;
        }*/
    }

    /*public String getLocalUserAppointmentID() {
          String dataToBeReturned = "";
          try {
              objDatabase = this.getReadableDatabase();
              String sqlQuery = "SELECT " + COLUMN_USER_APPOINTMENT_ID + " FROM " + USER_APPOINTMENTS_TABLE + " AS UA INNER JOIN " + USER_RECORD_TABLE + " AS UR " +
                      "WHERE UA." + USER_RECORD_ID + " = UR." + USER_RECORD_ID;
              Cursor cursor = objDatabase.rawQuery(sqlQuery, null);
              System.out.println("Query is " + sqlQuery);
              if (cursor.moveToFirst()) {
                  do {
                      dataToBeReturned = cursor.getString(cursor.getColumnIndex(COLUMN_USER_APPOINTMENT_ID));
                  }
                  while (cursor.moveToNext());
                  cursor.close();
              }
              objDatabase.close();
              return dataToBeReturned;
          } catch (Exception errorException) {
              errorException.printStackTrace();
              //Log.d("Exception occured", "Exception occured getUserRecordID"+errorException);
              objDatabase.close();
          }
          return dataToBeReturned;
      }*/
    val lastRecord: String
        get() {
            var dataToBeReturned = ""
            try {
                objDatabase = this.readableDatabase
                val sqlQuery = "SELECT " + SEARCH_NAME + " FROM " + RECENT_SEARCH + " LIMIT 1"
                val cursor = objDatabase?.rawQuery(sqlQuery, null)
                println("Query is $sqlQuery")
                println("cursor is " + cursor!!.count)
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        dataToBeReturned = cursor.getString(cursor.getColumnIndex(SEARCH_NAME))
                        println("dataToBeReturned = $dataToBeReturned")
                    } while (cursor.moveToNext())
                    cursor.close()
                }
                return dataToBeReturned
            } catch (errorException: Exception) {
                errorException.printStackTrace()
                //Log.d("Exception occured", "Exception occured getUserRecordID"+errorException);
                objDatabase!!.close()
            }
            return dataToBeReturned
        }

    //Block Table Retrieval
    /* public ArrayList<CalendarBlockListData> getBlockCalendarDetails(String parameterListId) {
        ArrayList<CalendarBlockListData> calenderListViewData = new ArrayList<CalendarBlockListData>();
        Cursor cursor = null;
        try {
            objDatabase = this.getReadableDatabase();
            if (!parameterListId.equalsIgnoreCase(""))
                cursor = objDatabase.rawQuery("SELECT * FROM " + BLOCK_CALENDAR_TABLE + " WHERE " + COLUMN_BLOCK_APPOINTMENT_ID + " IN ( SELECT  " + COLUMN_CALENDAR_SCHEDULE_ID + " FROM "
                        + APPOINTMENT_CALENDAR_TABLE + " WHERE " + COLUMN_CALENDAR_PARAMETER_LIST_VALUE_ID + " = " + parameterListId + ") AND  " + COLUMN_BLOCK_DELETE_STATUS + " = 'ACTIVE'", null);
            else
                cursor = objDatabase.rawQuery("SELECT * FROM " + BLOCK_CALENDAR_TABLE + " WHERE " + COLUMN_BLOCK_DELETE_STATUS + " = 'ACTIVE'", null);

            if (cursor.moveToFirst()) {
                do {

                    CalendarBlockListData listData = new CalendarBlockListData();
                    listData.setApplicableDays(cursor.getString(cursor.getColumnIndex(DatabaseOperations.COLUMN_BLOCK_CALENDAR_WORKING_DAYS)));
                    listData.setAppointmentId(cursor.getString(cursor.getColumnIndex(DatabaseOperations.COLUMN_BLOCK_APPOINTMENT_ID)));
                    listData.setBlockFromDate(cursor.getString(cursor.getColumnIndex(DatabaseOperations.COLUMN_BLOCK_CALENDAR_FROM_DATE)));
                    listData.setBlockToDate(cursor.getString(cursor.getColumnIndex(DatabaseOperations.COLUMN_BLOCK_CALENDAR_TO_DATE)));
                    listData.setBlockFromTime(cursor.getString(cursor.getColumnIndex(DatabaseOperations.COLUMN_BLOCK_CALENDAR_FROM_TIME)));
                    listData.setBlockToTime(cursor.getString(cursor.getColumnIndex(DatabaseOperations.COLUMN_BLOCK_CALENDAR_TO_TIME)));
                    listData.setCreatedDate(cursor.getString(cursor.getColumnIndex(DatabaseOperations.COLUMN_BLOCK_CALENDAR_CREATED_DATE)));
                    listData.setBlockReason(cursor.getString(cursor.getColumnIndex(DatabaseOperations.COLUMN_BLOCK_CALENDAR_REASON)));
                    listData.setNotes(cursor.getString(cursor.getColumnIndex(DatabaseOperations.COLUMN_BLOCK_CALENDAR_NOTIFICATION_MESSAGE)));
                    listData.setBlockScheduleId(cursor.getString(cursor.getColumnIndex(DatabaseOperations.COLUMN_BLOCK_SCHEDULE_ID)));
                    listData.setBlockedDays(cursor.getString(cursor.getColumnIndex(DatabaseOperations.COLUMN_BLOCK_CALENDAR_WORKING_DAYS)));
                    listData.setSyncStatus(cursor.getString(cursor.getColumnIndex(DatabaseOperations.COLUMN_BLOCK_SYNCHED)));
                    listData.setServerAppointmentId(cursor.getString(cursor.getColumnIndex(DatabaseOperations.COLUMN_BLOCK_SERVER_APPOINTMENT_ID)));
                    listData.setServerblockId(cursor.getString(cursor.getColumnIndex(DatabaseOperations.COLUMN_BLOCK_SERVER_ID)));
                    calenderListViewData.add(listData);

                } while (cursor.moveToNext());

            }
        } catch (Exception errorException) {
            Log.e("Block cal", errorException.getMessage());
        } finally {
            cursor.close();
            objDatabase.close();
            return calenderListViewData;
        }


    }*/
    //Block Table Retrieval
    //Block Table Insertion
    fun insertSearchDetails(searchName: String?) {
        val objValues: ContentValues
        try {
            objDatabase = this.writableDatabase
            objValues = ContentValues()
            objValues.put(SEARCH_NAME, searchName)
            if (numberOfRecordsInTable(RECENT_SEARCH) < 3) {
                objDatabase?.insert(RECENT_SEARCH, null, objValues)
                println("Inserted***************")
            } else {
                objDatabase?.update(
                    RECENT_SEARCH, objValues, SEARCH_ID + " = " +
                            lastRecord, null
                )
                println("Updated***************")
            }
            objDatabase?.close()
        } catch (errorException: Exception) {
            errorException.printStackTrace()
        }
    }

    private fun numberOfRecordsInTable(tableName: String): Int {
        val db = this.writableDatabase
        val mCount = db.rawQuery("select count(*) from $tableName", null)
        mCount.moveToFirst()
        val count = mCount.getInt(0)
        mCount.close()
        return count
    }

    fun deleteModuleTableData(tableName: String) {
        //Log.d("Debug-Text","Debug-Text,In deleteRecord(),TableName:"+tableName);
        try {
            val db = this.writableDatabase
            db.execSQL("DELETE FROM $tableName")
            db.close()
        } catch (errorException: Exception) {
            //Log.d("Debug Test", "Debug Test:Exception occured in deletRecord");
        }
    } /*
    public void updateSmsSettingDetails(String settingId, String confirmedSms, String cancelledSms, String reScheduleSms, String noShowSms, String completedSms, Context context) {
        ContentValues objValues;
        try {
            Log.e("updateAppointments ()", "StatusDetail Entered");
            objDatabase = this.getWritableDatabase();
            objValues = new ContentValues();
            objValues.put(SEND_CONFIRMED_SMS, confirmedSms);
            objValues.put(SEND_CANCELLED_SMS, cancelledSms);
            objValues.put(SEND_RESCHEDULED_SMS, reScheduleSms);
            objValues.put(SEND_NOSHOW_SMS, noShowSms);
            objValues.put(SEND_COMPLETED_SMS, completedSms);
            objDatabase.update(SMS_SETTINGS, objValues, SMS_SETTINGS_ID + " = " + settingId, null);
            System.out.println("Successfully updated SMS_SETTINGS with =" + settingId);
            objDatabase.close();

        } catch (Exception errorException) {
            errorException.printStackTrace();
        } finally {
            objDatabase.close();

        }
    }
*/

    // MY Clinic Module Methods-- End
    companion object {
        const val DATABASE_NAME = "Rider.db"
        const val YES = "Yes"

        // Block Calendar Column
        const val RECENT_SEARCH = "RECENT_SEARCH_TABLE"
        const val SEARCH_ID = "COLUMN_SEARCH_ID"
        const val SEARCH_NAME = "COLUMN_SEARCH_NAME"
        const val CREATE_RECENT_SEARCH_TABLE = ("CREATE TABLE IF NOT EXISTS " + RECENT_SEARCH +
                " (" + SEARCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SEARCH_NAME + " TEXT )")
        private const val DATABASE_VERSION = 1

        // Item Master Table Creation END
        private var mInstance: DatabaseOperations? = null
        fun round(value: Double /*, int places*/): String {
            val places = 2
            require(places >= 0)
            val df = DecimalFormat("#.##")
            return df.format(value)
        }

        fun getInstance(ctx: Context): DatabaseOperations? {
            if (mInstance == null) {
                mInstance = DatabaseOperations(ctx.applicationContext)
            }
            return mInstance
        }
    }
}