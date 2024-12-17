import { Component, ErrorInfo, ReactNode } from 'react';

/* 
 * How to use this Error Boundary:
 * 1. Wrap any component with <ErrorBoundary>
 * 2. Any error thrown in that component (e.g., throw new Error('test'))
 * 3. The error should be caught and "Something went wrong..." will be displayed
 * 4. Console should show error details
 */

interface Props {
    children?: ReactNode;
}

interface State {
    hasError: boolean;
}

class ErrorBoundary extends Component<Props, State> {
    public state: State = {
        hasError: false,
    };

    public static getDerivedStateFromError(_: Error): State {
        return { hasError: true };
    }

    componentDidCatch(error: Error, errorInfo: ErrorInfo): void {
        console.error('Uncaught error:', error, errorInfo);
    }

    public render() {
        if (this.state.hasError) {
            return <h1>Something went wrong...</h1>;
        }

        return this.props.children;
    }
}

export default ErrorBoundary;
