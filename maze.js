// Private class
class Point {
    constructor(x, y) {
        this.x = x;
        this.y = y;
    }
}

class Maze {
    constructor(conf) {
        this.grid = [];

        for (let i = 0; i < conf.numBlocksHigh; i++) {
            const row = [];
            for (let j = 0; j < conf.numBlocksWide; j++) {
                row.push(false);
            }
            this.grid.push(row);
        }
    }

    testCheckerBoard() {
        this.grid.forEach( (row, i) => {
            row.forEach( (cell, j) => {
                row[j] = (i % 2 === 0 && j % 2 === 0) || (i % 2 === 1 && j % 2 === 1);
            });
        });

        return this.grid;
    }

    regenerateMaze() {
        this.makeSuccessPath();

        return this.grid;
    }

    // Private methods
    makeSuccessPath() {
        const entryRow = this.grid.length - 1;
        const entryCell = Math.floor(Math.random() * this.grid[0].length);

        console.log('entryCell: ', entryCell);

        let currentPosition = new Point(entryCell, entryRow);
        let currentDirection;
        this.setPoint(currentPosition, true);

        console.log('startingPoint: ', currentPosition);

        let count = 0;

        while(currentPosition.y > 0) {
            const current = this.moveNextSuccessPath(currentPosition, currentDirection);
            // ### destructure?
            currentPosition = current.currentPosition;
            currentDirection = current.currentDirection;

            count++;

            if (count > 100) {
                console.log('hit max');
                break;
            }
        }
    }

    setPoint(point, value) {
        this.grid[point.y][point.x] = value;
    }

    moveNextSuccessPath(currentPosition, direction) {
        const height = this.grid.length;
        const width = this.grid[0].length;
        let nextPosition, range, offset;

        direction = this.nextDirection(direction);

        switch (direction) {
            case 'N':
                range = Math.min(currentPosition.y, 12);
                offset = Math.round(Math.random() * range);

                if (offset === 1) offset = 2;
                if (currentPosition.y < 3) offset = currentPosition.y;

                nextPosition = new Point(currentPosition.x, currentPosition.y - offset);

                for (let i = offset; i > 0; i-- ) {
                    this.setPoint(new Point(currentPosition.x, currentPosition.y - i), true);
                }

                break;
            case 'W':
                range = Math.min(currentPosition.x, 10);
                offset = Math.floor(Math.random() * range);
                if (offset < 3) offset = 3;
                if (currentPosition.x - offset < 0) offset = currentPosition.x;


                nextPosition = new Point(currentPosition.x - offset, currentPosition.y);
                if (currentPosition.x < 0) {
                    nextPosition.x = 0;
                    offset = currentPosition.x - nextPosition.x;
                }

                for (let i = offset; i > 0; i-- ) {
                    this.setPoint(new Point(currentPosition.x - i, currentPosition.y), true);
                }

                break;
            case 'E':
                range = Math.min(width - currentPosition.x, 10);
                offset = Math.floor(Math.random() * range);
                // ### current position is going greater than width, which then causes W to go less than zero
                if (offset < 3) offset = 3;
                if (currentPosition.x + offset > width) offset = width - 1;

                nextPosition = new Point(currentPosition.x + offset, currentPosition.y);
                if (currentPosition.x > width - 1) {
                    nextPosition.x = width - 1;
                    offset = nextPosition.x - currentPosition.x;
                }


                for (let i = 0; i <= offset; i++ ) {
                    this.setPoint(new Point(currentPosition.x + i, currentPosition.y), true);
                }

                break;
        }

        console.log('---------');
        console.log('direction: ', direction);
        console.log('  range: ', range);
        console.log('  offset: ', offset);
        console.log('nextPosition: ', nextPosition);



        return {
            currentPosition: nextPosition,
            currentDirection: direction
        };
    }

    nextDirection(lastDirection) {
        console.log('nextDirection(lastDirection): ', lastDirection);
        let next = 'N';

        if (lastDirection === 'N') {
            const flip = Math.round(Math.random());
            console.log('flip: ', flip);
            next = flip === 0 ? 'E' : 'W';
        }

        console.log('next: ', next);
        return next;
    }
}

function generateGrid(conf) {
    const maze = new Maze(conf);
    //return maze.testCheckerBoard();

    return maze.regenerateMaze();
}

// Draw the data structure on the screen
function dumpGrid(conf, grid) {
    const mazeWidth = conf.blockSizePx * conf.numBlocksWide;
    const mazeHeight = conf.blockSizePx * conf.numBlocksHigh;
    const maze = document.querySelector('.maze-container');

    maze.style.width = `${mazeWidth}px`;
    maze.style.height = `${mazeHeight}px`;

    const container = document.querySelector('.container');
    const containerWidth = mazeWidth * 1.2;
    container.style.width = `${containerWidth}px`;

    grid.forEach(row => {
        const mazeRow = document.createElement('div');
        mazeRow.classList.add('maze-row');

        row.forEach( cell => {
            const mazeCell = document.createElement('div');
            mazeCell.classList.add('maze-cell');
            mazeCell.classList.add( cell ? 'maze-path' : 'maze-block');
            mazeRow.appendChild(mazeCell);

            const blockSize = `${conf.blockSizePx}px`;
            mazeCell.style.width = blockSize;
            mazeCell.style.height = blockSize;
        });

        maze.appendChild(mazeRow);
    });
}

function drawMaze(conf) {
    const grid = generateGrid(conf);
    dumpGrid(conf, grid);
}

