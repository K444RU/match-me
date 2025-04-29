import {RecommendedUserDTO} from "@/api/types";
import {createSlice, PayloadAction} from "@reduxjs/toolkit";

interface UserCacheState {
    users: Record<number, RecommendedUserDTO>;
}

const initialState: UserCacheState = {
    users: {},
}

const userCacheSlice = createSlice({
    name: 'userCache',
    initialState,
    reducers: {
        setUsers: (state, action: PayloadAction<RecommendedUserDTO[]>) => {
            action.payload.forEach((user) => {
                state.users[user.userId] = user;
            });
        },
    },
});

export const { setUsers } = userCacheSlice.actions;
export default userCacheSlice.reducer;