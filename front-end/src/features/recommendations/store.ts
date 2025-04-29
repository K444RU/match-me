import { configureStore } from '@reduxjs/toolkit';
import userCacheReducer from './userCacheSlice';

export const store = configureStore({
    reducer: {
        userCache: userCacheReducer,
    },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;