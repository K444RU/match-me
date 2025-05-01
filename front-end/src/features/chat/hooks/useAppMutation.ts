import { DocumentNode, MutationHookOptions, MutationResult, OperationVariables, useMutation } from '@apollo/client';
import { useCallback } from 'react';
import { toast } from 'sonner';

type AppMutationTuple<TData, TVariables extends OperationVariables> = [
  (options?: Omit<MutationHookOptions<TData, TVariables>, 'mutation'>) => Promise<any>,
  MutationResult<TData>,
];

export function useAppMutation<TData = any, TVariables extends OperationVariables = OperationVariables>(
  mutation: DocumentNode,
  options?: MutationHookOptions<TData, TVariables>
): AppMutationTuple<TData, TVariables> {
  const [mutate, result] = useMutation<TData, TVariables>(mutation, {
    ...options,
    onError: (error) => {
      console.error('GraphQL Mutation Error:', error);
      toast.error(error.message || 'An error occurred during the operation. Please try again.');
      options?.onError?.(error);
    },
  });

  const enhancedMutate = useCallback(
    async (mutationOptions?: Omit<MutationHookOptions<TData, TVariables>, 'mutation'>) => {
      try {
        const mutationResult = await mutate(mutationOptions);
        return mutationResult;
      } catch (error) {
        console.error('Caught mutation error in enhancedMutate:', error);
        throw error;
      }
    },
    [mutate]
  );

  return [enhancedMutate, result];
}
