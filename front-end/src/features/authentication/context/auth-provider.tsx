import { LoginRequestDTO } from '@/api/types';
import { authService } from '@/features/authentication';
import { meService } from '@/features/user';
import { ReactNode, useState, useEffect, useCallback } from 'react';
import { AuthContext, User, LoginResult, AppError } from './auth-context';
import axios from 'axios';
import { STORAGE_KEYS } from '@/lib/constants/storageKeys';
interface AuthProviderProps {
	children: ReactNode;
}

export const AuthProvider = ({ children }: AuthProviderProps) => {
	const [isLoading, setIsLoading] = useState(true);
	const [user, setUser] = useState<User | null>(null);
	const [error, setError] = useState<AppError | null>(null);

	// Helper function to fetch user, update state, and handle token storage
	const updateUserFromToken = useCallback(async (token: string | null): Promise<User | null> => {
		if (!token) {
			console.debug('AuthProvider (updateUserFromToken): No token provided, clearing user.');
			localStorage.removeItem(STORAGE_KEYS.AUTH_TOKEN);
			localStorage.removeItem(STORAGE_KEYS.PROFILE_DATA);
			setUser(null);
			return null;
		}

		try {
			// Ensure token is stored before fetching user
			localStorage.setItem(STORAGE_KEYS.AUTH_TOKEN, token);
			console.debug('AuthProvider (updateUserFromToken): Token stored, fetching user...');
			const currentUser = await meService.getCurrentUser();
			setError(null);
			const userData: User = { ...currentUser, token };
			setUser(userData);
			console.debug('AuthProvider (updateUserFromToken): User fetch success:', currentUser.state);
			return userData;
		} catch (error) {
			console.error('AuthProvider (updateUserFromToken): User fetch failed, clearing token.', error);
			localStorage.removeItem(STORAGE_KEYS.AUTH_TOKEN);
			setUser(null);
			return null;
		}
	}, []); // Empty dependency array as it only uses setUser from useState

	// Initial load effect
	useEffect(() => {
		let isMounted = true;
		const initialLoad = async () => {
			console.debug('AuthProvider (Init): Starting initial load...');
			setIsLoading(true);
			const token = localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN);
			await updateUserFromToken(token);
			if (isMounted) {
				setIsLoading(false);
				console.debug('AuthProvider (Init): Loading finished.');
			}
		};

		initialLoad();

		return () => {
			isMounted = false;
		};
		// Include updateUserFromToken in dependency array because it's defined with useCallback
	}, [updateUserFromToken]);

	const login = async (credentials: LoginRequestDTO): Promise<LoginResult> => {
		setIsLoading(true);
		setError(null);
		console.debug('AuthProvider (Login): Attempting login...');
		try {
			const response = await authService.login(credentials);
			if (response?.token) {
				console.debug('AuthProvider (Login): Token received, updating user...');
				const userData = await updateUserFromToken(response.token);
				if (userData) {
					console.debug('✔️ AuthProvider (Login): Login success, user set:', userData.state);
					setIsLoading(false);
					return { success: true, user: userData };
				} else {
					console.warn('⚠️ AuthProvider (Login): Token received, but user fetch failed.');
					setIsLoading(false);
					const fetchError: AppError = { title: 'Login Issue', subtitle: 'Could not retrieve user details after login.' };
					setError(fetchError);
					return {
						success: false,
						error: fetchError,
					};
				}
			} else {
				console.warn('⚠️ AuthProvider (Login): No token in response from authService.login');
				await updateUserFromToken(null);
				setIsLoading(false);
				const tokenError: AppError = { title: 'Login Issue', subtitle: 'Login did not return a valid token.' };
				setError(tokenError);
				return {
					success: false,
					error: tokenError,
				};
			}
		} catch (error) {
			console.error('💥 AuthProvider (Login): Login error:', error);
			await updateUserFromToken(null); // Ensure user state is cleared

			let errorTitle = 'Login Failed';
			let errorSubtitle = 'An unexpected error occurred. Please try again.';
			let status: number | undefined;

			if (axios.isAxiosError(error)) {
				status = error.response?.status;
				if (status === 401) {
					errorTitle = 'Invalid Credentials';
					errorSubtitle = 'Please check your email and password';
				}
				// Add other status code handling if needed
			}

			const resultError: AppError = { title: errorTitle, subtitle: errorSubtitle, status };
			setError(resultError);
			setIsLoading(false); // Set loading false on error path
			return { success: false, error: resultError };
		}
	};

	// Updated logout function
	const logout = useCallback(() => {
		console.debug('AuthProvider (Logout): Logging out.');
		setError(null);
		updateUserFromToken(null); // Use helper to clear state and token
	}, [updateUserFromToken]);

	// Updated fetchCurrentUser function
	const fetchCurrentUser = useCallback(async () => {
		setIsLoading(true);
		console.debug('AuthProvider (fetchCurrentUser): Manual fetch initiated...');
		const token = localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN);
		if (!token) {
			console.debug('AuthProvider (fetchCurrentUser): No token found.');
			await updateUserFromToken(null); // Ensure state is cleared if token disappears
		} else {
			await updateUserFromToken(token);
		}
		setIsLoading(false);
		console.debug('AuthProvider (fetchCurrentUser): Loading finished.');
	}, [updateUserFromToken]);

	return <AuthContext.Provider value={{ user, isLoading, login, logout, fetchCurrentUser, error }}>{children}</AuthContext.Provider>;
};
