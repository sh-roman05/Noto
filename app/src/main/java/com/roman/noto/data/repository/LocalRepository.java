package com.roman.noto.data.repository;

import android.content.Context;

import com.roman.noto.util.AppExecutors;

/* Метод выдает актуальный экземпляр Repository */
public class LocalRepository {
    public static Repository getRepository(AppExecutors appExecutors, Context context) {
        return RoomRepository.getInstance(appExecutors, context);
    }
}
