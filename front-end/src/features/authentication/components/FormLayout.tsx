import React from 'react';

interface FormLayoutProps {
  title: string;
  children: React.ReactNode;
}

const FormLayout = ({ title, children }: FormLayoutProps) => {
  return (
    <>
      <div className="flex h-full items-center justify-center">
        <div className="bg-accent/50 mx-4 w-full max-w-md rounded-md p-6 sm:mx-0">
          <h1 className="mb-6 text-center text-3xl font-bold">
            {title}
          </h1>
          {children}
        </div>
      </div>
    </>
  );
};

export default FormLayout;
