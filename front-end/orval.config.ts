import { defineConfig } from 'orval';

// https://orval.dev/reference/configuration/overview
export default defineConfig({
    petstore: {
        output: {
            // https://orval.dev/reference/configuration/output#target
            target: 'src/api/api.ts',
            
            // https://orval.dev/reference/configuration/output#client
            client: 'axios',
            
            // https://orval.dev/reference/configuration/output#schemas
            schemas: 'src/api/types',

            // https://orval.dev/reference/configuration/output#mode
            mode: 'tags',

            // https://orval.dev/reference/configuration/output#baseurl
            baseUrl: 'http://localhost:8000/',

            // https://orval.dev/reference/configuration/output#mock
            mock: false, // enable/disable test mock generation

            // https://orval.dev/reference/configuration/output#prettier
            prettier: true,

            // https://orval.dev/reference/configuration/output#clean
            clean: true, // recreate the whole folder (avoid outdated files)
        },
        input: {
            // https://orval.dev/reference/configuration/input#target
            target: 'http://localhost:8000/v3/api-docs',

            // https://orval.dev/reference/configuration/input#validation
            // Väga perses tundub meil olevat console on täis punast ja kollast
            // aga tundub kasulik mingile standradile vastav api validation
            // validation: true, 

            // https://orval.dev/reference/configuration/input#filters
            filters: {
                mode: 'exclude',
                tags: ['test-controller'],
              },
        },
    },
});
