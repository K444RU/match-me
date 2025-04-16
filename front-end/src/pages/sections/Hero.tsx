import { Button } from '@/components/ui/button';
import { Login } from '@/features/authentication';
import { useAuth } from '@/features/authentication';
import { Link } from 'react-router-dom';

const Hero = ({ onLearnMore }: { onLearnMore: () => void }) => {
    const { user } = useAuth();
    return (
        <section className="bg-background h-screen">
            <div className="max-w-8xl mx-auto flex h-full flex-col items-center justify-center gap-4 px-4 py-16 text-center sm:px-6 lg:px-8">
                <h1 className="text-text font-extrabold sm:text-5xl lg:text-8xl">
                    The{' '}
                    <span className="text-accent underline">
                        data-driven
                    </span>{' '}
                    solution to endless swiping
                </h1>
                <p className="max-w-7xl text-4xl font-light tracking-tight">
                    Get connections with people that actually match your
                    interests. One flat rate for everyone - no premium tiers, no
                    pay-to-date schemes.
                </p>
                <div className="mt-6 flex flex-wrap space-x-2">
                    {!user ? (
                        <Login />
                    ) : (
                        <Link to="/chats">
                            <Button className="font-semibold tracking-wide">
                                Go to Chats
                            </Button>
                        </Link>
                    )}
                    <Button
                        onClick={onLearnMore}
                        variant="secondary"
                        className="font-semibold tracking-wide"
                    >
                        Learn more
                    </Button>
                </div>
            </div>
        </section>
    );
};

export default Hero;
