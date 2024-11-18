import Login from './Login';

const Hero = ({
  title = 'Amazing title',
  subtitle = 'even more amazing subtitle.',
  onLearnMore,
}: {
  title?: string;
  subtitle?: string;
  onLearnMore: () => void;
}) => {
  return (
    <section className="h-screen bg-background">
      <div className="mx-auto flex h-full max-w-7xl flex-col items-center justify-center px-4 sm:px-6 lg:px-8">
        <div className="text-center">
          <h1 className="md: text-6xl font-extrabold text-text sm:text-5xl">
            {title}
          </h1>
          <p className="my-4 text-xl text-text">{subtitle}</p>
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
      </div>
    </section>
  );
};

export default Hero;
