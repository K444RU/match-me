import {
  DocumentNode,
  OperationVariables,
  SubscriptionHookOptions,
  SubscriptionResult,
  useSubscription,
} from '@apollo/client';
import { useEffect } from 'react';
import { toast } from 'sonner';

// Define a clearer type for the hook's return value
type AppSubscriptionResult<TData, TVariables extends OperationVariables> = SubscriptionResult<TData, TVariables>;

export function useAppSubscription<TData = any, TVariables extends OperationVariables = OperationVariables>(
  subscription: DocumentNode,
  options?: SubscriptionHookOptions<TData, TVariables>
): AppSubscriptionResult<TData, TVariables> {
  const { error, ...result } = useSubscription<TData, TVariables>(subscription, {
    ...options,
    onData: (data) => {
      options?.onData?.(data);
    },
  });

  useEffect(() => {
    if (error) {
      console.error('GraphQL Subscription Error:', error);
      toast.error(`Subscription error: ${error.message} ${error.stack}`);
    }
  }, [error]);

  return {
    ...result,
    error,
  };
}
