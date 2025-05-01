// Example: src/hooks/useAppQuery.ts
import { DocumentNode, OperationVariables, QueryHookOptions, QueryResult, useQuery } from '@apollo/client';
import { useEffect } from 'react';
import { toast } from 'sonner';

type AppQueryResult<TData, TVariables extends OperationVariables> = QueryResult<TData, TVariables>;

export function useAppQuery<TData = any, TVariables extends OperationVariables = OperationVariables>(
  query: DocumentNode,
  options?: QueryHookOptions<TData, TVariables>
): AppQueryResult<TData, TVariables> {
  const { error, ...result } = useQuery<TData, TVariables>(query, {
    ...options,
  });

  useEffect(() => {
    if (error) {
      console.error('GraphQL Query Error:', error);
      toast.error('An error occurred while fetching data. Please try again.');
    }
  }, [error]);

  return {
    ...result,
    error,
  };
}
