import Login from '@/features/authentication/components/PopUpForm';
import { useAuth } from '@/features/authentication/AuthContext';
import { Link } from 'react-router-dom';

const Hero = ({ onLearnMore }: { onLearnMore: () => void }) => {
  const { user } = useAuth();
  return (
    <section className="h-screen bg-background">
      <div className="max-w-8xl mx-auto flex h-full flex-col items-center justify-center gap-4 px-4 py-16 text-center sm:px-6 lg:px-8">
        <h1 className="font-extrabold text-text sm:text-5xl lg:text-8xl">
          The <span className="text-accent-500 underline">data-driven</span> solution to
          endless swiping
        </h1>
        <p className="max-w-7xl text-4xl font-light tracking-tight text-text">
          Get connections with people that actually match your interests. One
          flat rate for everyone - no premium tiers, no pay-to-date schemes.
        </p>
        <div className="mt-6 flex flex-wrap space-x-2">
          {!user ? (
            <Login />
          ) : (
            <Link to="/chats">
              <button className="rounded-md border-2 border-primary bg-primary px-5 py-2 font-semibold tracking-wide text-background transition-colors duration-300 hover:bg-transparent hover:text-primary">
                Go to Chats
              </button>
            </Link>
          )}
          <button
            onClick={onLearnMore}
            className="rounded-md border-2 border-primary-200 bg-primary-200 px-5 py-2 font-semibold tracking-wide text-text transition-colors duration-300 hover:border-primary-400 hover:bg-primary-400 hover:text-text"
          >
            Learn more
          </button>
        </div>
      </div>
    </section>
  );
};

export default Hero;
