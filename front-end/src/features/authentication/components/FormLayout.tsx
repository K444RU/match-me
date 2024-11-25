import React from 'react';

interface FormLayoutProps {
  title: string;
  children: React.ReactNode;
}

const FormLayout = ({ title, children }: FormLayoutProps) => {
  return (
    <>
      <div className="flex h-screen items-center justify-center bg-background">
        <div className="w-full max-w-md rounded-md bg-accent-200 p-6">
          <h1 className="mb-6 text-center text-3xl font-bold text-text">
            {title}
          </h1>
          {children}
        </div>
      </div>
    </>
  );
};

export default FormLayout;
