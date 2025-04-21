import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { cn } from '@/lib/utils';
import React from 'react';

interface FormLayoutProps {
  title: string;
  children: React.ReactNode;
  className?: string;
}

export default function FormLayout({ title, children, className }: FormLayoutProps) {
  return (
    <div className={cn(`flex h-full items-center justify-center`, className)}>
      <Card className="w-[400px]">
        <CardHeader>
          <CardTitle className="text-center">
            <h1 className="text-3xl font-bold">{title}</h1>
          </CardTitle>
        </CardHeader>
        <CardContent>{children}</CardContent>
      </Card>
    </div>
  );
};
