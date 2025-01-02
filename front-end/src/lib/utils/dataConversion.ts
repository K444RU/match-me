import { HOBBIES } from "@/assets/hobbies";
import { Option } from "@/components/ui/multi-select";
import { Hobby } from "@/types/api";

export const hobbiesToOptions = (hobbies: Hobby[] | undefined): Option[] => 
    hobbies?.map((hobby) => ({
        value: hobby.id.toString(),
        label: hobby.name,
        group: hobby.group,
    })) || [];

export const optionsToHobbies = (options: Option[]): Hobby[] => 
    options.map((opt) => {
        const hobby = HOBBIES.find((h) => h.id.toString() === opt.value);
        if (!hobby) throw new Error('Invalid hobby option');
        return hobby;
    });

export const hobbiesById = (ids: number[]): Hobby[] => 
    ids.map((id) => {
        const hobby = HOBBIES.find((h) => h.id === id);
        if (!hobby) throw new Error('Invalid hobby option');
        return hobby;
    });