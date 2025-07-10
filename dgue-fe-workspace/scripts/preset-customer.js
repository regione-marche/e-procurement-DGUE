const projectName = 'app-dgue';
const path = require('path');

const CUSTOMERS_FOLDER = path.join('.', 'clienti');

const shell = require('shelljs'),
    chalk = require('chalk'),
    _ = require('lodash'),
    readline = require('readline'),
    CUSTOMERS = require(path.join('..', CUSTOMERS_FOLDER, 'customers')).CUSTOMERS,
    argv = require('yargs').argv,
    fs = require('fs');

const BASE_PROJECT_ASSETS_FOLDER = path.join('.', 'projects');
const PROJECTS = shell.ls(BASE_PROJECT_ASSETS_FOLDER);
const DELETE_FOLDERS = ['footer', 'header', 'base-css.css'];
const EXCLUDED_FOLDERS = ['environments'];
const ENV_BASE_FILE_NAME = 'env.js';

// args
if (argv == null || (_.isArray(argv) && _.isEmpty(argv))) {
    shell.echo(chalk.red(`No args provided!`));
    process.exit(1);
}

const defaultCustomer = 'appalti-contratti';
const argsCustomer = argv.customer;

// apro il readline
const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

// scelgo il customer
start();

function start() {


    let currentCustomer;

    if (argsCustomer != null) {
        if (!containsCustomer(argsCustomer)) {
            shell.echo(chalk.red(`E' stato selezionato un cliente sconosciuto!`));
            closeReadline();
            process.exit(1);
        }
        currentCustomer = argsCustomer;    
    }

      
    // chain di domande
    Promise.resolve()
        .then(() => {
            return currentCustomer == null ? promptCustomerQuestion() : Promise.resolve({ currentCustomer, asked: false });
        })
        .then(({ answer, asked }) => {
            if (asked) {
                if (!answer) {
                    shell.echo(chalk.red(`Nessun cliente selezionato!`));
                    closeReadline();
                    process.exit(-1);
                }
                if (!_.has(CUSTOMERS, answer)) {
                    shell.echo(chalk.red(`E' stato selezionato un cliente sconosciuto!`));
                    closeReadline();
                    process.exit(-1);
                }
                currentCustomer = _.get(CUSTOMERS, answer).code;                
            }
            return Promise.resolve();
        })
        
        .then(() => {
            // eseguo la copia del customer
            if(currentCustomer != null){
                shell.echo(chalk.red(`Copio il customer ${currentCustomer}!`));
                copyCustomer(defaultCustomer, currentCustomer);
            }
           
            
           
            closeReadline();
        })
        .catch((error) => {
            console.error(error);
            process.exit(-1);
        });
}

function promptCustomerQuestion() {
    const question =
        `\nSeleziona il cliente da utilizzare?
${formatChoices(CUSTOMERS)}`;

    return new Promise((resolve, reject) => {
        rl.question(question, (answer) => {
            resolve({ answer, asked: true });
        });
    });
}


function copyCustomer(defaultCustomer, currentCustomer) {

    // delete folders
    shell.ls(BASE_PROJECT_ASSETS_FOLDER).forEach(function (project) {
        if(projectName == project){          
           
            const DELETE_PROJECT_FOLDER = path.join(BASE_PROJECT_ASSETS_FOLDER,'app-dgue','src','app','layout','base','/');
            const DELETE_PROJECT_FOLDER_FAVICON = path.join(BASE_PROJECT_ASSETS_FOLDER,'app-dgue','src','favicon.ico');
            
            //shell.echo(chalk.red(`Deleting assets folders in ${DELETE_PROJECT_FOLDER_FOOTER}`));
            //shell.echo(chalk.red(`Deleting assets folders in ${DELETE_PROJECT_FOLDER_HEADER}`));
            //shell.echo(chalk.red(`Deleting assets folders in ${DELETE_PROJECT_FOLDER_CSS}`));
            _.each(DELETE_FOLDERS, (folder) => {
                shell.echo(chalk.red(`Deleting assets folders in ${DELETE_PROJECT_FOLDER}${folder}`));
                shell.rm('-Rf', path.join(DELETE_PROJECT_FOLDER, folder));
            });
            shell.echo(chalk.red(`Deleting ${DELETE_PROJECT_FOLDER_FAVICON}`));
            shell.rm('-Rf', path.join(DELETE_PROJECT_FOLDER_FAVICON));
        }
        
    });

    // copy default
    shell.echo(chalk.blue(`COPYING --> ${defaultCustomer}`));
    shell.ls(CUSTOMERS_FOLDER).forEach(function (customer) {
        if (customer === defaultCustomer) {
                              
            const FOLDER_TO_COPY = path.join('clienti', defaultCustomer,'src','app','layout','base', '/*');
            const PROJECT_ASSETS_FOLDER = path.join(BASE_PROJECT_ASSETS_FOLDER,'app-dgue','src','app','layout','base', '/');

            const FOLDER_TO_COPY_ENV = path.join('clienti', defaultCustomer,'src','environments', '/*');
            const PROJECT_ASSETS_FOLDER_ENV = path.join(BASE_PROJECT_ASSETS_FOLDER,'app-dgue','src','environments', '/');
            
            const FOLDER_TO_COPY_FAVICON = path.join('clienti', defaultCustomer,'favicon.ico');       
            const PROJECT_ASSETS_FOLDER_FAVICON = path.join(BASE_PROJECT_ASSETS_FOLDER,'app-dgue','src', '/');            
                                                             
            shell.echo(chalk.yellow(`Copying content from ${FOLDER_TO_COPY} to ${PROJECT_ASSETS_FOLDER}`));
            shell.cp('-R', FOLDER_TO_COPY, PROJECT_ASSETS_FOLDER);
            shell.cp(FOLDER_TO_COPY_FAVICON, PROJECT_ASSETS_FOLDER_FAVICON);
            shell.cp(FOLDER_TO_COPY_ENV, PROJECT_ASSETS_FOLDER_ENV);
            shell.echo(chalk.green(`Copy success!`));
                     
                
         
        }
    });

    // copy customer
    if (currentCustomer != null && currentCustomer != defaultCustomer) {
        shell.echo(chalk.blue(`COPYING --> ${currentCustomer}`));
        // ciclo i customer
        shell.ls(CUSTOMERS_FOLDER).forEach(function (customer) {
            if (customer === currentCustomer) {
                const PROJECTS_FOLDER = path.join(CUSTOMERS_FOLDER, customer);
                // ciclo i progetti                                                                           
                const FOLDER_TO_COPY = path.join('clienti', currentCustomer,'src','app','layout','base', '/*');
                const PROJECT_ASSETS_FOLDER = path.join(BASE_PROJECT_ASSETS_FOLDER,'app-dgue','src','app','layout','base', '/');
                
                const FOLDER_TO_COPY_ENV = path.join('clienti', currentCustomer,'src','environments', '/*');
                const PROJECT_ASSETS_FOLDER_ENV = path.join(BASE_PROJECT_ASSETS_FOLDER,'app-dgue','src','environments', '/');

                const FOLDER_TO_COPY_FAVICON = path.join('clienti', currentCustomer,'favicon.ico');       
                const PROJECT_ASSETS_FOLDER_FAVICON = path.join(BASE_PROJECT_ASSETS_FOLDER,'app-dgue','src', '/'); 
                                                                                                  
                shell.echo(chalk.yellow(`Copying content from ${FOLDER_TO_COPY} to ${PROJECT_ASSETS_FOLDER}`));
                shell.cp('-R', FOLDER_TO_COPY, PROJECT_ASSETS_FOLDER);
                shell.cp(FOLDER_TO_COPY_FAVICON, PROJECT_ASSETS_FOLDER_FAVICON);
                shell.cp(FOLDER_TO_COPY_ENV, PROJECT_ASSETS_FOLDER_ENV);
                shell.echo(chalk.green(`Copy success!`));
                            
                    
               
            }
        });
    }
}




function formatChoices(originalChoices) {
    let choices = '';
    _.each(_.sortBy(_.keys(originalChoices), (key) => key), (key) => {
        let choice = _.get(originalChoices, key);
        choices += `${key}) ${choice.description}\n`;
    });
    return choices;
}

function containsCustomer(curCus) {
    let found = false;
    _.each(CUSTOMERS, (value, key) => {
        if (value != null) {
            found = (value.code === curCus);
            if (found === true) {
                return false;
            }
        }
    });
    return found;
}

function containsProject(curPrj) {
    let found = false;
    const elem = PROJECTS.find((one) => {
        return one === curPrj;
    });
    found = (elem != null);
    return found;
}




function closeReadline() {
    try {
        rl.close()
    } catch (er) {

    }
}