"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const server_1 = __importDefault(require("./classes/server"));
const environment_1 = require("./global/environment");
const router_1 = __importDefault(require("./routes/router"));
const server = new server_1.default;
server.app.use('/', router_1.default);
server.start(() => {
    console.log(`Servidor corriendo en el puerto ${environment_1.SERVER_PORT}`);
});
