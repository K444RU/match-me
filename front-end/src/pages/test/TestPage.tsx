import { Button } from '@/components/ui/button';
import {
    Card,
    CardContent,
    CardDescription,
    CardFooter,
    CardHeader,
    CardTitle,
} from '@/components/ui/card';
import { Toaster } from '@/components/ui/sonner';
import { useAuth } from '@/features/authentication';
import {
    generateMessagesSendRequestDTO,
    generateSignupRequestDTO,
    generateUserPairs,
    generateUserParametersRequestDTO,
} from '@/mocks/FakerService';
import axios from 'axios';
import { toast } from 'sonner';
import { useState } from 'react';

const API_URL = `${import.meta.env.VITE_API_URL}/test/`;

export default function TestPage() {
    const { user } = useAuth();
    const [emailsInput, setEmailsInput] = useState('');

    const bulkCreateUsers = async () => {
        try {
            const initialUsers = generateSignupRequestDTO(100);
            const emails = initialUsers.map((user) => user.email);

            await axios.post(`${API_URL}users`, initialUsers, {
                headers: { Authorization: `Bearer ${user?.token}` },
            });
            toast.success('Users created successfully');

            const userParameters = generateUserParametersRequestDTO(100);
            await axios.post(
                `${API_URL}users/finish`,
                {
                    parameters: userParameters,
                    emails: emails,
                },
                {
                    headers: { Authorization: `Bearer ${user?.token}` },
                }
            );
            toast.success('User parameters set successfully');

            const userConnections = generateUserPairs(0, 15, emails);
            const generatedConnections = await axios.post(
                `${API_URL}connections`,
                userConnections,
                {
                    headers: { Authorization: `Bearer ${user?.token}` },
                }
            );
            toast.success('User connections added successfully');

            const userMessages = generateMessagesSendRequestDTO(
                0,
                100,
                generatedConnections.data
            );
            await axios.post(`${API_URL}messages`, userMessages, {
                headers: { Authorization: `Bearer ${user?.token}` },
            });
            toast.success('User messages added successfully');
        } catch (error) {
            console.error('Error creating users:', error);
            toast.error('Failed to create users');
        }
    };

    const bulkDeleteUsers = async () => {
        try {
            let emails;
            try {
                emails = JSON.parse(emailsInput);
                if (!Array.isArray(emails)) {
                    throw new Error('Input must be an array of emails');
                }
            } catch (parseError) {
                toast.error('Invalid JSON input. Please check the format.');
                return;
            }

            await axios.delete(`${API_URL}users`, {
                data: emails,
                headers: { Authorization: `Bearer ${user?.token}` },
            });

            toast.success('Users deleted successfully');
        } catch (error) {
            console.error('Error deleting users:', error);
            toast.error('Failed to delete users');
        }
    };

    return (
        <div className="h-screen w-full bg-background-900 px-8 py-8">
            <Card className="w-[350px] bg-white">
                <CardHeader>
                    <CardTitle>Create project</CardTitle>
                    <CardDescription>
                        Deploy your new project in one-click.
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <div className="grid w-full items-center gap-4">
                        <div className="flex flex-col space-y-1.5">
                            <Button onClick={bulkCreateUsers}>
                                Create 100 Users
                            </Button>
                        </div>
                    </div>
                </CardContent>
                <CardFooter className="flex justify-between">
                    <Button variant="outline">Cancel</Button>
                    <Button>Deploy</Button>
                </CardFooter>
            </Card>
            <Card className="w-[350px] bg-white">
                <CardHeader>
                    <CardTitle>Delete Users</CardTitle>
                    <CardDescription>
                        Paste the array of emails to delete users from the
                        system.
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <div className="grid w-full items-center gap-4">
                        <div className="flex flex-col space-y-1.5">
                            <textarea
                                className="border-input focus-visible:ring-ring min-h-[200px] w-full rounded-md border bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2"
                                placeholder="Paste email array here..."
                                value={emailsInput}
                                onChange={(e) => setEmailsInput(e.target.value)}
                            />
                            <Button
                                onClick={bulkDeleteUsers}
                                variant="destructive"
                                disabled={!emailsInput.trim()}
                            >
                                Delete Users
                            </Button>
                        </div>
                    </div>
                </CardContent>
            </Card>
            <Toaster className="bg-black text-white" />
        </div>
    );
}
