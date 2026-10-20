import useAuth from "@/auth/store";
import { refreshToken } from "@/services/AuthService";
import axios, {
  type AxiosError,
  type InternalAxiosRequestConfig,
} from "axios";
import toast from "react-hot-toast";

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api/v1",
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
  timeout: 10000,
});

type RetryConfig = InternalAxiosRequestConfig & {
  _retry?: boolean;
};

let isRefreshing = false;
let pending: Array<(newToken: string | null) => void> = [];

function queueRequest(callback: (newToken: string | null) => void) {
  pending.push(callback);
}

function resolveQueue(newToken: string | null) {
  pending.forEach((callback) => callback(newToken));
  pending = [];
}

apiClient.interceptors.request.use((config) => {
  const accessToken = useAuth.getState().accessToken;

  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`;
  }

  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const original = error.config as RetryConfig | undefined;
    const is401 = error.response?.status === 401;
    const isAuthRequest = original?.url?.includes("/auth/");

    if (!original || !is401 || original._retry || isAuthRequest) {
      const responseData = error.response?.data as
        | { message?: string }
        | undefined;
      toast.error(responseData?.message || "Unable to complete the request");
      return Promise.reject(error);
    }

    original._retry = true;

    if (isRefreshing) {
      return new Promise((resolve, reject) => {
        queueRequest((newToken) => {
          if (!newToken) {
            reject(new Error("Session refresh failed"));
            return;
          }

          original.headers.Authorization = `Bearer ${newToken}`;
          resolve(apiClient(original));
        });
      });
    }

    isRefreshing = true;

    try {
      const loginResponse = await refreshToken();
      const newToken = loginResponse.accessToken;

      if (!newToken) {
        throw new Error("No access token received");
      }

      useAuth
        .getState()
        .changeLocalLoginData(
          loginResponse.accessToken,
          loginResponse.user,
          true,
        );

      resolveQueue(newToken);
      original.headers.Authorization = `Bearer ${newToken}`;
      return apiClient(original);
    } catch (refreshError) {
      resolveQueue(null);
      await useAuth.getState().logout(true);
      return Promise.reject(refreshError);
    } finally {
      isRefreshing = false;
    }
  },
);

export default apiClient;
