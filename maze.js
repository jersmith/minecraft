// The data structure is much easier to deal with
function generateGrid() {
    const grid = [];

    for (let i = 0; i < 150; i++) {
        const row = [];
        for (let j = 0; j < 50; j++) {
            row.push((i % 2 === 0 && j % 2 === 0) || (i % 2 === 1 && j % 2 === 1));
        }
        grid.push(row);
    }

    return grid;
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
    const grid = generateGrid();
    dumpGrid(conf, grid);
}

