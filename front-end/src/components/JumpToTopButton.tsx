import { FaArrowUp } from 'react-icons/fa';

const JumpToTopButton = () => {
  return (
    <div
      onClick={() => {
        window.scrollTo({ top: 0, behavior: 'smooth' });
      }}
      className="fixed bottom-5 right-5"
    >
      <button className="rounded-full bg-primary p-4 text-xl font-semibold text-background opacity-50 transition-opacity duration-300 hover:opacity-100">
        <FaArrowUp />
      </button>
    </div>
  );
};

export default JumpToTopButton;
