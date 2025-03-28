/**
 * Generated by orval v7.3.0 🍺
 * Do not edit manually.
 * Blind API
 * kood/Jõhvi match-me task API
 * OpenAPI spec version: v0.0.1
 */
import * as axios from 'axios';
import type { AxiosRequestConfig, AxiosResponse } from 'axios';
import type { GenderTypeDTO } from './types';

export const getUserGenderTypeController = () => {
  const getAllGenders = <TData = AxiosResponse<GenderTypeDTO[]>>(options?: AxiosRequestConfig): Promise<TData> => {
    return axios.default.get(`http://localhost:8000/api/genders`, options);
  };
  return { getAllGenders };
};
export type GetAllGendersResult = AxiosResponse<GenderTypeDTO[]>;
