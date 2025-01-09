import { HOBBIES } from "@/assets/hobbies";
import { Option } from "@/components/ui/multi-select";

export const hobbiesById = (ids: number[]): Option[] => 
    ids.map((id) => {
        const hobby = HOBBIES.find((h) => h.value === id.toString());
        if (!hobby) throw new Error('Invalid hobby option');
        return hobby;
    });