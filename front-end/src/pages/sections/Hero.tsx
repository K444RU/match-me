import Login from '@/features/authentication/components/PopUpForm';

const Hero = ({ onLearnMore }: { onLearnMore: () => void }) => {
  return (
    <section className="h-screen bg-background">
      <div className="mx-auto flex h-full max-w-4xl flex-col items-center justify-center gap-4 px-4 py-16 text-center sm:px-6 lg:px-8">
        <h1 className="text-4xl font-extrabold text-text sm:text-5xl">
          The <span className="text-accent-500">data-driven</span> solution to
          endless swiping
        </h1>
        <p className="max-w-xl text-xl font-light tracking-tighter text-text">
          Get connections with people that actually match your interests. One
          flat rate for everyone - no premium tiers, no pay-to-date schemes.
        </p>
        <div className="flex space-x-2">
          <Login />
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
